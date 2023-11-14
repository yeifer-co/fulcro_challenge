# fulcro_challenge
Building a ToDo app using Fulcro

## Prerequisites

For this project you will need to have on your system:

- Java
- Clojure
- Leiningen
- Node.js
- npm
- Datomic Peer

## Challenges requirements

Build a  Todo APP using Fulcro 

- [x] Todo app should be able to list the todos, create one, edit one, delete one, anything else fun you can think of.
- [x] Make fulcro defsc components for the components you need like todo todo-list etc.
    - A component has
        - Ident
        - Initial-state ( at first for testing, then df/load will fill in the data )
        - Query

- [x] Build the rest of the fulcro components you need
- [x] Then make mutations in front end to save data to the fulcro state map
- [ ] Connect with backend to save data with mutations and retrieve data with resolvers and load data with df/load
- [ ] Install datomic and save data in the backend to datomic database

## Architecture understanding

Fulcro is a full-stack web framework, these are the main components:

![Fulcro architecture](doc/diagrams/architecture.png)

Frontend:

a. UI - Fulcro/React components rendered a DOM and submit mutations to the backend to the Transaction Subsystem (Tx)

b. Tx - The Transaction Subsystem is responsible for receiving mutations from the UI and asynchronously executes local mutations and sends remote mutations to the backend.

c. Local DB - The Local DB is a local in-memory database that holds the application state. (Tx) typically schedules a re-render afterwards. The cached data is turned into a data tree according to the needs of the UI, to feed and render the UI.

Backend:

a. Pathom - Is a Clojure(Script) library that provides a query language (EQL) and a query engine. It is used to query the backend database and return the data to the frontend.

b. Datomic - Is a distributed database that stores data as facts (Inmutable data). It is used to store the application data.

## Datomic research

In the context of Clojure and Datomic, there are different libraries and tools available for working with Datomic databases. Let's discuss the differences between `datascript/datascript`, `com.datomic/client-pro`, and `com.datomic/peer`.

#### datascript/datascript:

- **Type:** Datascript is an in-memory database written in ClojureScript that provides a Datalog query language.
- **Use Case:** It's often used for front-end development where you need a client-side database with a query language similar to Datomic.
- **Storage:** Datascript stores data in memory, making it suitable for client-side applications with smaller datasets.
- **Query Language:** Datalog, a declarative query language similar to Datomic's.

#### com.datomic/client-pro:

- **Type:** The Datomic Client Pro library is part of Datomic Cloud, which is a cloud-based version of Datomic.
- **Use Case:** It's used for building applications that interact with Datomic Cloud. Datomic Cloud is a fully managed service provided by Cognitect.
- **Connection:** It connects to Datomic Cloud over the network, allowing you to interact with a Datomic database hosted in the cloud.
- **Features:** It includes features like database functions, schema management, and transactions.

#### com.datomic/peer:

- **Type:** The Datomic Peer library is used for connecting to an on-premise Datomic database (Datomic on-prem).
- **Use Case:** It's used when you want to connect to a Datomic database that you manage locally or on your own infrastructure.
- **Connection:** It connects directly to the Datomic storage service, allowing you to interact with a Datomic database that can be hosted on your own infrastructure.
- **Features:** It provides features for querying, transactions, and schema management similar to the client-pro library.

## Helpful links

#### FULCRO Reading over fulcro

Overview (EQL, Pathom, Fulcro) -> https://fulcro-community.github.io/guides/tutorial-eql-pathom-overview/index.html

https://book.fulcrologic.com/

Mutations -> https://book.fulcrologic.com/#_mutations

Idents -> https://book.fulcrologic.com/#_idents
    - https://book.fulcrologic.com/#_passing_callbacks_and_other_parent_computed_data
    - https://book.fulcrologic.com/#_automatic_normalization

Initial state -> https://book.fulcrologic.com/#_initial_state
    - https://book.fulcrologic.com/#_initial_state

Idents and queries -> https://book.fulcrologic.com/#_idents_as_a_query_element

Componets rendering -> https://book.fulcrologic.com/#_components_and_rendering

Query -> https://book.fulcrologic.com/#_query

#### FULCRO inspect

https://chrome.google.com/webstore/detail/fulcro-inspect/meeijplnfjcihnhkpanepcaffklobaal

#### PATHOM Reading over pathom

Pathom -> https://blog.wsscode.com/pathom/v2/pathom/2.2.0/connect/basics.html

https://github.com/wilkerlucio/pathom

EQL -> http://edn-query-language.org/

Implementing Om.next -> https://medium.com/@wilkerlucio/implementing-custom-om-next-parsers-f20ca6db1664

#### DATOMIC install this database

https://docs.datomic.com/pro/getting-started/brief-overview.html

dev setup: https://docs.datomic.com/pro/getting-started/dev-setup.html
