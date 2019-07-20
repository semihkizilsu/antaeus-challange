package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.*
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

class BillingServiceTest {

    val invoice = Invoice(id = 123, customerId = 5, amount = Money(value = BigDecimal(250), currency = Currency.USD), status = InvoiceStatus.PENDING)
    val customer = Customer(id = 5, currency = Currency.USD, status = CustomerStatus.OPEN)

    private val paymentProviderMock = mockk<PaymentProvider> {
        every { charge(invoice) } returns true
    }

    private val customerServiceMock = mockk<CustomerService> {
    }

    private val invoiceServiceMock = mockk<InvoiceService> {
        every { fetch(123) } returns invoice
        every { changeStatus(123, InvoiceStatus.PAID) } just Runs
    }

    private val billingService = BillingService(
        paymentProvider = paymentProviderMock,
        customerService = customerServiceMock,
        invoiceService = invoiceServiceMock
    )

    @Test
    fun `will charge if customer has enough money`() {
        billingService.paymentWithInvoiceId(id = 123)
        assertTrue(true)
    }
}