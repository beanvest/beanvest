## Usage examples

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,Val,Cost,Profit
  ```
  ```
  account              Deps    Wths   Intr   Fees   Div    Profit  Cost     Value
  .*                   25,420   -140    352      0      0     344  -25,287  25,632
  saving:.*             6,420   -140    352      0      0     344   -6,287   6,632
  saving:regularSaver   3,000      0    162      0      0     162   -3,000   3,162
  saving:savings        3,420   -140    190      0      0     183   -3,287   3,470
  trading              19,000      0      0      0      0      -0  -19,000  19,000
  ```
- Print cash stats on holdings, accounts and groups
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Val,Cost,Profit --report-holdings
  ```
  ```
  account                  Deps    Wths   Profit  Cost     Value
  .*                       25,420   -140     344  -25,287  25,632
  saving:.*                 6,420   -140     344   -6,287   6,632
  saving:regularSaver       3,000      0     162   -3,000   3,162
  saving:regularSaver:GBP   3,000      0     162   -3,000   3,162
  saving:savings            3,420   -140     183   -3,287   3,470
  saving:savings:GBP        3,420   -140     183   -3,287   3,470
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
  .*                  │ 24,240   -120 │ 20,700   -100 │ 17,160    -80 │ 12,870    -60 │ 8,580    -40 │ 4,290    -20 │
  saving:.*           │  6,240   -120 │  5,700   -100 │  5,160    -80 │  3,870    -60 │ 2,580    -40 │ 1,290    -20 │
  saving:regularSaver │  3,000      0 │  3,000      0 │  3,000      0 │  2,250      0 │ 1,500      0 │   750      0 │
  saving:savings      │  3,240   -120 │  2,700   -100 │  2,160    -80 │  1,620    -60 │ 1,080    -40 │   540    -20 │
  trading             │ 18,000      0 │ 15,000      0 │ 12,000      0 │  9,000      0 │ 6,000      0 │ 3,000      0 │
  ```
