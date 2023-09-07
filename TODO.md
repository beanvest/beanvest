## TODO
- **TestResults**: store test results, parse it, put it in an sqlite db and check what changed since previous test run
- **ProperParser**: scrap current regexp-based parser and use some decent parser generator (Antlr?)
- **Server**: add `beanvest ui` running a webserver with `/options` and `/report`. Load journals once.
- **UI**: new custom UI displaying cumulative and periodic reports
- **MultipleCurrencies**: add currency conversion to the reports (eg `--currency=GBP`)
- **LongSamples**: generate sample journals covering longer periods (eg 20 years)
- **BeancountUI**: journal UI through export to beancount format and display with fava
- **ClosedAccountsSubgroup** aggregate closed accounts in each group instead of hiding them
  - they are important piece of data; they have impact on cumulative stats of the group

## Bugs
- Cost calculation for credit account vs regular account that went below zero might be weird
