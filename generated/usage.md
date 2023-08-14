## Usage examples

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,Val,Cost,Profit
  ```
  ```
  account              Deps    Wths   Intr   Fees   Div    Profit  Cost     Value
  .*                   22,000      0    162      0      0     162  -22,000  22,162
  saving:.*             3,000      0    162      0      0     162   -3,000   3,162
  saving:regularSaver   3,000      0    162      0      0     162   -3,000   3,162
  trading              19,000      0      0      0      0      -0  -19,000  19,000
  ```
- Print cash stats on holdings, accounts and groups
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Val,Cost,Profit --report-holdings
  ```
  ```
  account                  Deps    Wths   Profit  Cost     Value
  .*                       22,000      0     162  -22,000  22,162
  saving:.*                 3,000      0     162   -3,000   3,162
  saving:regularSaver       3,000      0     162   -3,000   3,162
  saving:regularSaver:GBP   3,000      0     162   -3,000   3,162
  trading                  19,000      0      -0  -19,000  19,000
  trading:GBP              19,000      0       0   -1,000   1,000
  trading:SPX                   0      0      -0  -18,000  18,000
  ```
- Print cumulative deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample --end=2023-07-01 --columns deps,wths --interval=quarter
  ```
  ```
                      ╷ 23q2          ╷ 23q1          ╷ 22q4          ╷ 22q3          ╷ 22q2         ╷ 22q1         ╷
  account             │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps   Wths  │ Deps   Wths  │
  .*                  │ 21,000      0 │ 18,000      0 │ 15,000      0 │ 11,250      0 │ 7,500      0 │ 3,750      0 │
  saving:.*           │  3,000      0 │  3,000      0 │  3,000      0 │  2,250      0 │ 1,500      0 │   750      0 │
  saving:regularSaver │  3,000      0 │  3,000      0 │  3,000      0 │  2,250      0 │ 1,500      0 │   750      0 │
  trading             │ 18,000      0 │ 15,000      0 │ 12,000      0 │  9,000      0 │ 6,000      0 │ 3,000      0 │
  ```
