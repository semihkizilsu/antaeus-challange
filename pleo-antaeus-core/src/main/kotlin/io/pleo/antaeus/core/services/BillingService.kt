package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.CustomerStatus
import io.pleo.antaeus.core.exceptions.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val customerService: CustomerService,
    private val invoiceService: InvoiceService
) {

    private fun payment(invoice: Invoice) : Boolean {

        try {
            return when{
                paymentProvider.charge(invoice = invoice) -> {
                    invoiceService.changeStatus(id = invoice.id, status = InvoiceStatus.PAID)
                    true
                }
            else -> {
                customerService.changeCustomerStatus(id = invoice.customerId, status = CustomerStatus.TEMPORARYCLOSED)
                false
                }
            }
        }
        catch(e: Exception) { 
            when(e){
                is CustomerNotFoundException -> {
                    //if provider return this exception contact with customer and open new account for customer
                    customerService.changeCustomerStatus(id = invoice.customerId, status = CustomerStatus.CLOSED)
                }
                is CurrencyMismatchException -> {
                    //customerCurrencyCode not equals invoiceCurrencyCode
                    //STEP1: currency conversion service will be integrate
                    //STEP2: invoiceAmount and invoiceCurrencyCode will be update to customerCurrencyCode with new service
                    //STEP3: call payment amount again with new calculated amount and curreny
                }
                else -> {
                    //Network exception handle
                }
            }

            return false
        }
    }

    fun paymentWithInvoiceId(id: Int){
        val invoice = invoiceService.fetch(id = id)

        when(invoice.status){
            InvoiceStatus.PENDING ->
                if(!payment(invoice)) 
                    throw CheckCustomerStatusAndTryAgainException(invoice.customerId)
            else -> throw InvoiceNotApplicableException(invoice.id)
        }
    }

    //charges all pending invoices and no need any return type
    fun payments() {
        val pendingInvoices  = invoiceService.fetchAll(status = InvoiceStatus.PENDING)
       
        pendingInvoices.forEach { item ->
            payment(item)
        }
    }
}