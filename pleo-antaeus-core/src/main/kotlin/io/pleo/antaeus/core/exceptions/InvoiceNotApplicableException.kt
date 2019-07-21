package io.pleo.antaeus.core.exceptions

class InvoiceNotApplicableException(invoiceId: Int) :
    Exception("Status of invoice '$invoiceId' does not suitable to charge!")
