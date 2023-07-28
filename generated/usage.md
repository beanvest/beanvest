## Usage examples

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample/* --group
  ```
  ```
  account              opened      closed  deps    wths   div    intr   fees   rGain  cash    uGain  eVal   aGain  xirr
  .*                   2022-01-01  -       22,990   -140      0    475    -70      0  23,255      0      0    405    2.2
  saving:.*            2022-01-01  -        5,320   -140      0    475      0      0   5,655      0      0    475   11.1
  saving:regularSaver  2022-01-01  -        1,900      0      0    285      0      0   2,185      0      0    285   18.1
  saving:savings       2022-01-01  -        3,420   -140      0    190      0      0   3,470      0      0    190    7.0
  trading              2022-01-01  -       17,670      0      0      0    -70      0  17,600      0      0    -70   -0.5
  ```
- Print cumulative deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample/* --group --columns deps,wths --interval=quarter
  ```
  ```
                      ╷ 23q2          ╷ 23q1          ╷ 22q4          ╷ 22q3          ╷ 22q2         ╷ 22q1         ╷
  account             │ deps    wths  │ deps    wths  │ deps    wths  │ deps    wths  │ deps   wths  │ deps   wths  │
  .*                  │ 21,780   -120 │ 18,150   -100 │ 14,520    -80 │ 10,890    -60 │ 7,260    -40 │ 3,630    -20 │
  saving:.*           │  5,040   -120 │  4,200   -100 │  3,360    -80 │  2,520    -60 │ 1,680    -40 │   840    -20 │
  saving:regularSaver │  1,800      0 │  1,500      0 │  1,200      0 │    900      0 │   600      0 │   300      0 │
  saving:savings      │  3,240   -120 │  2,700   -100 │  2,160    -80 │  1,620    -60 │ 1,080    -40 │   540    -20 │
  trading             │ 16,740      0 │ 13,950      0 │ 11,160      0 │  8,370      0 │ 5,580      0 │ 2,790      0 │
  ```
- Print changes in deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample/* --group --columns deps,wths --interval=quarter --delta
  ```
  ```
                      ╷ 23q2         ╷ 23q1         ╷ 22q4         ╷ 22q3         ╷ 22q2         ╷ 22q1         ╷
  account             │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │ Δdeps  Δwths │
  .*                  │ 3,630    -20 │ 3,630    -20 │ 3,630    -20 │ 3,630    -20 │ 3,630    -20 │ 3,630    -20 │
  saving:.*           │   840    -20 │   840    -20 │   840    -20 │   840    -20 │   840    -20 │   840    -20 │
  saving:regularSaver │   300      0 │   300      0 │   300      0 │   300      0 │   300      0 │   300      0 │
  saving:savings      │   540    -20 │   540    -20 │   540    -20 │   540    -20 │   540    -20 │   540    -20 │
  trading             │ 2,790      0 │ 2,790      0 │ 2,790      0 │ 2,790      0 │ 2,790      0 │ 2,790      0 │
  ```
- Inspect journals with daily cumulative stats
  ```bash
  beanvest journal sample/* | tail -n 20
  ```
  ```
  2023-10-01 fee  10
    stats: dep: 26620, wth: -160, int: 550, fee: -80, div: 0, rga: 0.00, csh: 26930
    holdings: 0.00 GBP []
  
  2023-11-01 deposit 100 GBP
  2023-11-01 interest 15 GBP
  2023-11-01 deposit 180 GBP
  2023-11-01 interest 10 GBP
  2023-11-01 deposit 930 GBP
    stats: dep: 27830, wth: -160, int: 575, fee: -80, div: 0, rga: 0.00, csh: 28165
    holdings: 0.00 GBP []
  
  2023-12-01 deposit 100 GBP
  2023-12-01 interest 15 GBP
  2023-12-01 deposit 180 GBP
  2023-12-01 interest 10 GBP
  2023-12-01 deposit 930 GBP
    stats: dep: 29040, wth: -160, int: 600, fee: -80, div: 0, rga: 0.00, csh: 29400
    holdings: 0.00 GBP []
  ```
