package io.pleo.antaeus.core.exceptions

class CheckCustomerStatusAndTryAgainException(customerId: Int) :
    Exception("Please check customer status of customer '$customerId' and try again after required changes done.")
