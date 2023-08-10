## Usage examples

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,RGain,UGain,Val,profit,xirr
  ```
  ```
  account              Deps    Wths   Intr   Fees   Xirr   RGain  UGain  Div    Profit  Value
  .*                   24,090   -140    352    -70      0      0      0      0     274  24,232
  saving:.*             6,420   -140    352      0      0      0      0      0     344   6,632
  saving:regularSaver   3,000      0    162      0      0      0      0      0     162   3,162
  saving:savings        3,420   -140    190      0      0      0      0      0     183   3,470
  trading              17,670      0      0    -70     -0      0      0      0     -70  17,600
  ```
- Print cumulative deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample --columns deps,wths --interval=quarter
  ```
  ```
                      ╷ 23q2          ╷ 23q1          ╷ 22q4          ╷ 22q3          ╷ 22q2         ╷ 22q1         ╷
  account             │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps   Wths  │ Deps   Wths  │
  .*                  │ 22,980   -120 │ 19,650   -100 │ 16,320    -80 │ 12,240    -60 │ 8,160    -40 │ 4,080    -20 │
  saving:.*           │  6,240   -120 │  5,700   -100 │  5,160    -80 │  3,870    -60 │ 2,580    -40 │ 1,290    -20 │
  saving:regularSaver │  3,000      0 │  3,000      0 │  3,000      0 │  2,250      0 │ 1,500      0 │   750      0 │
  saving:savings      │  3,240   -120 │  2,700   -100 │  2,160    -80 │  1,620    -60 │ 1,080    -40 │   540    -20 │
  trading             │ 16,740      0 │ 13,950      0 │ 11,160      0 │  8,370      0 │ 5,580      0 │ 2,790      0 │
  ```
