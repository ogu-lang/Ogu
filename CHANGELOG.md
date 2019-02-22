# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [0.2.1] - 2019-2-22

### Fixed

- Refactor Parser
- Refactor Lexer
- remove duplicated code

## [0.2.0] - 2019-2-21

### Changed

- new parser in scala, but runtime still is clojure
- function definitions require def keyword
- val is marked deprecated, in next release will be removed
- many minors changes in syntax

### Removed

- begin/end, now you must indent like python/haskell.


## [0.1.5] - 2017-11-4

### Added

- more support for ADT (see demos/adt.ogu   )

## [0.1.4] - 2017-10-30

### Added

- bash command for ogu
- ogu.turtle module
- use sentence

## [0.1.3] - 2017-10-29

### Added

- regular expressions
- operators over regular expression
- Algebraics data types

### Changed

- Coments now begin with --, like haskell
- glue operator now is ` or ´ 

## [0.1.2] - 2017-10-29

### Added

- Reify
- notes on english and spanish 
- exception handling

### Changed

- type replaced by class and record

## [0.1.1] - 2017-03-01

### Changed

- Add vars
- Fix issues with grammar on comp-expr
- add more text to OGU-0.1 notes.

## [0.1.0] - 2017-02-26

### Changed

- First compiler and interpreter based on Clojure runtime


[Unreleased]: https://github.com/your-name/ogu-lang/compare/0.1.1...HEAD
[0.1.2]: https://github.com/your-name/ogu-lang/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/your-name/ogu-lang/compare/0.1.0...0.1.1
