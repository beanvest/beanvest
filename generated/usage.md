## Usage examples

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,Value,Cost,Profit,rgain,ugain
  ```
  ```
  Account              Deps    Wths   Div    Intr   Fees   Value   Cost     Profit  RGain  UGain
  .*                   25,420   -140      0    352      0  26,970  -25,287   1,682      0  1,338
  saving:.*             6,420   -140      0    352      0   6,632   -6,287     344      0      0
  saving:regularSaver   3,000      0      0    162      0   3,162   -3,000     162      0      0
  saving:savings        3,420   -140      0    190      0   3,470   -3,287     183      0      0
  trading              19,000      0      0      0      0  20,338  -19,000   1,338      0  1,338
  ```
- Print cash stats on holdings, accounts and groups
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Value,Cost,Profit --report-holdings
  ```
  ```
  Account                  Deps    Wths   Value   Cost     Profit
  .*                       25,420   -140  26,970  -25,287   1,682
  saving:.*                 6,420   -140   6,632   -6,287     344
  saving:regularSaver       3,000      0   3,162   -3,000     162
  saving:regularSaver:GBP   3,000      0   3,162   -3,000     162
  saving:savings            3,420   -140   3,470   -3,287     183
  saving:savings:GBP        3,420   -140   3,470   -3,287     183
  trading                  19,000      0  20,338  -19,000   1,338
  trading:GBP              19,000      0   1,000   -1,000       0
  trading:SPX                   0      0  19,338  -18,000   1,338
  ```
- Print cumulative deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample --end=2023-07-01 --columns deps,wths --interval=quarter
  ```
  ```
                      ╷ 23q2          ╷ 23q1          ╷ 22q4          ╷ 22q3          ╷ 22q2         ╷ 22q1         ╷
  Account             │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps   Wths  │ Deps   Wths  │
  .*                  │ 24,240   -120 │ 20,700   -100 │ 17,160    -80 │ 12,870    -60 │ 8,580    -40 │ 4,290    -20 │
  saving:.*           │  6,240   -120 │  5,700   -100 │  5,160    -80 │  3,870    -60 │ 2,580    -40 │ 1,290    -20 │
  saving:regularSaver │  3,000      0 │  3,000      0 │  3,000      0 │  2,250      0 │ 1,500      0 │   750      0 │
  saving:savings      │  3,240   -120 │  2,700   -100 │  2,160    -80 │  1,620    -60 │ 1,080    -40 │   540    -20 │
  trading             │ 18,000      0 │ 15,000      0 │ 12,000      0 │  9,000      0 │ 6,000      0 │ 3,000      0 │
  ```
