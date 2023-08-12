## TODO
- **TestResults**: store test results, parse it, put it in an sqlite db and check what changed since previous test run
- **UI**: new custom UI displaying cumulative and periodic reports
- **pXirr**: calculate periodic xirr showing return in each interval (eg `--column=pXirr --deltas --interval=year`)
- **MutlipleCurrencies**: add currency conversion to the reports (eg `--currency=GBP`)
- **StockSamples**: generate sample journals with stock buys and sells with changing prices
- **LongSamples**: generate sample journals covering longer periods (eg 20 years)
- **BeancountUI**: journal UI through export to beancount format and display with fava
- **ProperParser**: scrap current regexp-based parser and use some decent parser generator (Antlr?)

## Bugs
- ProcessRunner hangs when there is too much output