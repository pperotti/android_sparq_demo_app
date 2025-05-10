# Sparq Demo Application
This readme file is a template for the information contained by the demo application

## Table of Contents

- [Introduction](#introduction)
- [Key Technical Decisions](#key-technical-decisions)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Introduction

The application created in this repo is a demo application which shows a modern solution to a regular
android app in 2025. 

## Key Technical Decisions

This application make use of: 
* Jetpack Compose
* Kotlin
* Coroutines
* Google's Architecture Guidelines
* Navigation
* Unidirectional Data Flow
* Multi-Module Support.
* Dependency Injection using Hilt
* Database Support using Room
* Support for different layouts depending on the device orientation and screen size.
* Extension functions to handle simple mapping functions. 

The application organizes the code in two modules: one to encapsulate the data and another one to 
encapsulate all the UI. The data module is independent from the UI one.

The data module exposes its functionality via a Repository and list of Item entities. Those entities 
are independent from where such items are coming from. I introduced here the concept of "remote" entities
and "storage/local" entities. These entities and the mapping between them may look like an over-design
for the scope of the demo app but it is the right approach to use that scales well when real APIs are in use. 

The Repository exposed by the data module uses two sources of data. One to deal with the remote api
and another one to deal with the way they are stored in the local database. 

For the scope of the exercise, I only use a boolean to determine which source should be used
to load the data. A more robust mechanism to determine which mechanism should be used may involve
persisting a timestamp on a separate table and check 'how old' is the persisted data and based on this
value decide which source should be used.

All the UI is written using Jetpack Compose. Different Composables were introduced in order to manage
error cases, loading scenarios, different layouts depending on the space available and orientation.

Finally, all the UI follows the declarative approach promoted by Jetpack Compose, which means
that elements are drawn on the screen according depending on a well-defined state. 

## Getting Started

### Prerequisites

List any prerequisites for running the project. This could include Android Studio, specific SDK
versions, etc.

- Java 11 or higher
- Android Studio 2024 or superior
- Android SDK version 33
- Target SDK version 35

### Installation

Provide step-by-step instructions on how to set up and run your project.

1. Clone the repository or Open the zip version of the project.   
2. Open the project in Android Studio.
3. Build and run the application on an emulator or connected device.

## Associated Projects

The user can check a sibling project with a sightly different approach also written
for the purpose of android interviews.

The repo can be found at: git@github.com:pperotti/android-tmdb-app.git

## License

This project is licensed under the [MIT License](https://choosealicense.com/licenses/mit/) - see the
[LICENSE.md](https://github.com/username/repository/blob/master/LICENSE.md) file for details.

## Acknowledgments

All the project created here was done by Pablo Perotti

- [github.com/pperotti] (GitHub)

```