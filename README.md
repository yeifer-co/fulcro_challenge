# fulcro_challenge
Building a ToDo app using Fulcro

## Prerequisites

For this project you will need to have on your system:

- Java
- Clojure
- Leiningen
- Node.js
- npm

## Challenges requirements

Build a  Todo APP using Fulcro 

- [ ] Todo app should be able to list the todos, create one, edit one, delete one, anything else fun you can think of.
- [ ] Make fulcro defsc components for the components you need like todo todo-list etc.
    - A component has
        - Ident
        - Initial-state ( at first for testing, then df/load will fill in the data )
        - Query

- [ ] Build the rest of the fulcro components you need
- [ ] Then make mutations in front end to save data to the fulcro state map
- [ ] Connect with backend to save data with mutations and retrieve data with resolvers and load data with df/load
- [ ] Install datomic and save data in the backend to datomic database

## Helpful links

#### FULCRO Reading over fulcro

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

https://github.com/wilkerlucio/pathom

EQL -> http://edn-query-language.org/

Implementing Om.next -> https://medium.com/@wilkerlucio/implementing-custom-om-next-parsers-f20ca6db1664

#### DATOMIC install this database

https://docs.datomic.com/pro/getting-started/brief-overview.html

dev setup: https://docs.datomic.com/pro/getting-started/dev-setup.html
