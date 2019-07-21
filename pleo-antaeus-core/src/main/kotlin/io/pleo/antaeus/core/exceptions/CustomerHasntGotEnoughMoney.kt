package io.pleo.antaeus.core.exceptions

class CustomerHasntGotEnoughMoney(customerId: Int) : 
    Exception("Customer '$customerId' hasn't got enough money to pay the invoice.")