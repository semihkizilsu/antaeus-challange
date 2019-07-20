/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class InvoiceService(private val dal: AntaeusDal) {

    fun fetchAll(status: InvoiceStatus? = null): List<Invoice>{
        status?.let{
            return dal.fetchInvoicesWithStatus(status = status)
        } ?: run {
            return dal.fetchInvoices()
        }
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun changeStatus(id: Int, status: InvoiceStatus){
        dal.changeInvoiceStatus(id = id, status = status)
    }
}
