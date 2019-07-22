## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will pay those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

### Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|
|       Packages containing the main() application. 
|       This is where all the dependencies are instantiated.
|
├── pleo-antaeus-core
|
|       This is where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|
|       Module interfacing with the database. Contains the models, mappings and access layer.
|
├── pleo-antaeus-models
|
|       Definition of models used throughout the application.
|
├── pleo-antaeus-rest
|
|        Entry point for REST API. This is where the routes are defined.
└──
```

## Instructions
Fork this repo with your solution. We want to see your progression through commits (don’t commit the entire solution in 1 step) and don't forget to create a README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

Happy hacking 😁!

## How to run
```
./docker-start.sh
```

## Libraries currently in use
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library

## What I've Done
First of all it was very satisfied challenge to me because I've never use Kotlin as a programing language, mockk as a mocking framework and Javalin as a REST framework before. I aimed develop a successfully working version of the challange. My first commits are about the main structure and the latests are to improve the code quality. Challenge takes between 20 - 25 hours for me. For this challenge I studied Kotlin syntax a few hours. 

* Invoice amounts were supposed as subscription fees.
* Payment process was thought as scheduled. There could be a scheduled service which calls /rest/v1/payments POST API at every first day of the months. If there will be any error while charging process service will log the error automatically. 
* /rest/v1/payments/{:id} POST API charges given invoice. This one can be called from any UI application.
* Both APIs check the status of invoice before charging process and return 201 status code for each successed transaction. 
* Customer status added to the Customer model. In some cases customer status could be updated to TEMPORARYCLOSED or CLOSED.
* Exception and REST status codes were handled in the REST API layer. Some examples added and these can be improved.
* Some test methods are developed for testing BillingService. New methods can be developed to improve coverage.
* CurrencyConverter service can be implemented to handle when payment provider throws CurrencyMismatchException exception. This service can convert invoice's amount and currency to customer's currency. Then charge process of invoice can be retried.