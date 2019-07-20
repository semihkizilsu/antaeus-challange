package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.CustomerStatus
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.NetworkException

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
                    //SK: invoiceStatus could change to UNCHARGED
                    customerService.changeCustomerStatus(id = invoice.customerId, status = CustomerStatus.CLOSED)
                }
                is CurrencyMismatchException -> {
                    //SK: customerCurrencyCode not equals invoiceCurrencyCode
                    //STEP1: currency conversion service will be integrate
                    //STEP2: invoiceAmount and invoiceCurrencyCode will be update to customerCurrencyCode with new service
                }
                else -> {
                    //Network exception handle
                }
            }

            return false
        }
    }

    fun paymentWithInvoiceId(id: Int) : Boolean{
        val invoice = invoiceService.fetch(id = id)

        return when(invoice.status){
            InvoiceStatus.PENDING -> payment(invoice)
            else -> throw Exception() //throw InvoiceNotApplicableException(id)// TODO: 400 BadRequest
        }
    }

    fun payments() {
        val pendingInvoices  = invoiceService.fetchAll(status = InvoiceStatus.PENDING)
       
        pendingInvoices.forEach { item ->
            payment(item)
        }
    }
}