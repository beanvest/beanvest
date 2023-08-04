## Usage examples

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample --end=2023-07-01
  ```
  ```
  account              opened      closed  deps    wths   div    intr   fees   rGain  cash    uGain  hVal   aGain  xirr   xirrp
  .*                   2022-01-01  -       24,090   -140      0    352    -70      0  24,232      0      0    282    1.5    1.5
  saving:.*            2022-01-01  -        6,420   -140      0    352      0      0   6,632      0      0    352    6.3    6.3
  saving:regularSaver  2022-01-01  -        3,000      0      0    162      0      0   3,162      0      0    162    5.2    5.2
  saving:savings       2022-01-01  -        3,420   -140      0    190      0      0   3,470      0      0    190    7.7    7.7
  trading              2022-01-01  -       17,670      0      0      0    -70      0  17,600      0      0    -70   -0.5   -0.5
  ```
- Print cumulative deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample --columns deps,wths --interval=quarter
  ```
  ```
                      ╷ 23q2          ╷ 23q1          ╷ 22q4          ╷ 22q3          ╷ 22q2         ╷ 22q1         ╷
  account             │ deps    wths  │ deps    wths  │ deps    wths  │ deps    wths  │ deps   wths  │ deps   wths  │
  .*                  │ 22,980   -120 │ 19,650   -100 │ 16,320    -80 │ 12,240    -60 │ 8,160    -40 │ 4,080    -20 │
  saving:.*           │  6,240   -120 │  5,700   -100 │  5,160    -80 │  3,870    -60 │ 2,580    -40 │ 1,290    -20 │
  saving:regularSaver │  3,000      0 │  3,000      0 │  3,000      0 │  2,250      0 │ 1,500      0 │   750      0 │
  saving:savings      │  3,240   -120 │  2,700   -100 │  2,160    -80 │  1,620    -60 │ 1,080    -40 │   540    -20 │
  trading             │ 16,740      0 │ 13,950      0 │ 11,160      0 │  8,370      0 │ 5,580      0 │ 2,790      0 │
  ```
- Print changes in deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample --columns deps,wths --interval=quarter --delta
  ```
  ```
                      ╷ 23q2         ╷ 23q1         ╷ 22q4         ╷ 22q3         ╷ 22q2         ╷ 22q1         ╷
  account             │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │
  .*                  │ 3,330    -20 │ 3,330    -20 │ 4,080    -20 │ 4,080    -20 │ 4,080    -20 │ 4,080    -20 │
  saving:.*           │   540    -20 │   540    -20 │ 1,290    -20 │ 1,290    -20 │ 1,290    -20 │ 1,290    -20 │
  saving:regularSaver │     0      0 │     0      0 │   750      0 │   750      0 │   750      0 │   750      0 │
  saving:savings      │   540    -20 │   540    -20 │   540    -20 │   540    -20 │   540    -20 │   540    -20 │
  trading             │ 2,790      0 │ 2,790      0 │ 2,790      0 │ 2,790      0 │ 2,790      0 │ 2,790      0 │
  ```
