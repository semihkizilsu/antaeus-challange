package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.CustomerStatus
import io.pleo.antaeus.core.exceptions.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val customerService: CustomerService,
    private val invoiceService: InvoiceService
) {

    private fun pay(invoice: Invoice) : ExceptionHandler {

        try {
            return when{
                paymentProvider.charge(invoice = invoice) -> {
                    invoiceService.changeStatus(id = invoice.id, status = InvoiceStatus.PAID)
                    ExceptionHandler.Success(value = true)
                }
            else -> {
                customerService.changeCustomerStatus(id = invoice.customerId, status = CustomerStatus.TEMPORARYCLOSED)
                ExceptionHandler.Error(cause = CustomerHasntGotEnoughMoney(customerId = invoice.customerId))
                }
            }
        }
        catch(e: Exception) { 
            return when(e){
                is CustomerNotFoundException -> {
                    //if provider return this exception contact with customer and open new account for customer
                    customerService.changeCustomerStatus(id = invoice.customerId, status = CustomerStatus.CLOSED)
                    ExceptionHandler.Error(cause = CheckCustomerStatusAndTryAgainException(customerId = invoice.customerId))
                }
                is CurrencyMismatchException -> {
                    /* customerCurrencyCode not equals invoiceCurrencyCode
                        STEP1: currency conversion service will be integrate
                        STEP2: invoiceAmount and invoiceCurrencyCode will be update to customerCurrencyCode with new service
                        STEP3: call payment amount again with new calculated amount and currency
                    */
                    ExceptionHandler.Error(cause = CurrencyMismatchException(invoiceId = invoice.id, customerId = invoice.customerId))
                }
                else -> {
                    //Network exception handle
                    ExceptionHandler.Error(cause = NetworkException())
                }
            }
        }
    }

    //if someone wants to charge of an invoice this method can be used
    fun payWithInvoiceId(id: Int) {
        val invoice = invoiceService.fetch(id = id)

        when(invoice.status){
            InvoiceStatus.PENDING -> {
                val paymentResult = pay(invoice)
                when(paymentResult){
                    is ExceptionHandler.Error -> throw paymentResult.cause 
                }
            }
            else -> throw InvoiceNotApplicableException(invoiceId = invoice.id)
        }
    }

    //charges all pending invoices and no need any return type
    fun payAllInvoices() {
        val pendingInvoices  = invoiceService.fetchAll(status = InvoiceStatus.PENDING)
       
        pendingInvoices.forEach { invoice ->
            val paymentResult = pay(invoice)
                when(paymentResult){
                    is ExceptionHandler.Error -> logger.error(paymentResult.cause) {
                         paymentResult.cause.message }
                }
        }
    }
}