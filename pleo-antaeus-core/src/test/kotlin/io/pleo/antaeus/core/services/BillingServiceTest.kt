package io.pleo.antaeus.core.services

import io.mockk.*
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.exceptions.CheckCustomerStatusAndTryAgainException
import io.pleo.antaeus.models.*
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*

class BillingServiceTest {

    val invoice = Invoice(
        id = 123, 
        customerId = 5, 
        amount = Money(value = BigDecimal(250), currency = Currency.USD), 
        status = InvoiceStatus.PENDING)

    val customer = Customer(id = 5, currency = Currency.USD, status = CustomerStatus.OPEN)

    private val paymentProviderMock = mockk<PaymentProvider> {
       
    }

    private val customerServiceMock = mockkClass(CustomerService::class) {
    }

    private val invoiceServiceMock = mockkClass(InvoiceService::class)  {
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
        every { paymentProviderMock.charge(invoice) } returns true
        billingService.paymentWithInvoiceId(id = 123)

        assertTrue(true)
    }

    @Test
    fun `not able to charge if customer hasn't enough money`() {
        every { paymentProviderMock.charge(invoice) } returns false
        every { customerServiceMock.changeCustomerStatus(id = invoice.customerId, status = CustomerStatus.TEMPORARYCLOSED)} just Runs
        
        assertThrows<CheckCustomerStatusAndTryAgainException> {
            billingService.paymentWithInvoiceId(id = 123)
        }
    }
}