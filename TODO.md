## TODO
- currency conversion
- calculate periodic xirr showing return in each interval (`Pxirr` and `ΔPxirr`?)
- add combining columns that are missing since the refactor (`dw`: deps+wths; `value`: cash+holdingsValue, `hValue`: holdingsValue)
- generate sample journals with stock buys and sells
- generate sample journals covering longer periods
- print descriptive warnings in stdErr in case of prices missing and maybe other errors
- journal UI through export beancount format and display with fava
- new custom UI

## bugs
- empty json returned in the terminal with --json flag in native build
- cumulative values shown in the terminal instead of deltas