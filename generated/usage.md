## Usage examples
Journals used to generate these reports are in `sample/` directory.

- Print various stats for all accounts and groups on each level of the accounts for whole period
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Div,Intr,Fees,Value,Cost,Profit,rgain,ugain
  ```
  ```
  Account               Deps    Wths    Div    Intr   Fees   Value   Cost     Profit  RGain  UGain
  .*                    55,259  -3,223    150    366    -27  54,969  -52,272   2,696      0  2,417
  saving:.*             11,670  -3,223      0    366      0   8,813   -8,537     276      0      0
  saving:regularSaver    3,000  -3,083      0     83      0       0        0       0      0      0
  saving:regularSaver2   5,250       0      0     93      0   5,343   -5,250      93      0      0
  saving:savings         3,420    -140      0    190      0   3,470   -3,287     183      0      0
  trading:.*            43,589       0    150      0    -27  46,156  -43,735   2,421      0  2,417
  trading:index         34,089       0      0      0      0  36,392  -34,089   2,303      0  2,303
  trading:risky          9,500       0    150      0    -27   9,764   -9,646     117      0    114
  ```
- Print cash stats on holdings, accounts and groups
  ```bash
  beanvest returns sample --end=2023-07-01 --columns=Deps,Wths,Value,Cost,Profit --report-holdings
  ```
  ```
  Account                   Deps    Wths    Value   Cost     Profit
  .*                        55,259  -3,223  54,969  -52,272   2,696
  saving:.*                 11,670  -3,223   8,813   -8,537     276
  saving:regularSaver        3,000  -3,083       0        0       0
  saving:regularSaver2       5,250       0   5,343   -5,250      93
  saving:regularSaver2:GBP   5,250       0   5,343   -5,250      93
  saving:regularSaver:GBP    3,000  -3,083       0        0       0
  saving:savings             3,420    -140   3,470   -3,287     183
  saving:savings:GBP         3,420    -140   3,470   -3,287     183
  trading:.*                43,589       0  46,156  -43,735   2,421
  trading:index             34,089       0  36,392  -34,089   2,303
  trading:index:GBP         34,089       0   1,767   -1,767       0
  trading:index:SPX              0       0  34,625  -32,322   2,303
  trading:risky              9,500       0   9,764   -9,646     117
  trading:risky:GBP          9,500       0     650     -646       4
  trading:risky:RSK              0       0   9,114   -9,000     114
  ```
- Print cumulative deposits and withdrawals for accounts and groups for each quarter
  ```bash
  beanvest returns sample --end=2023-07-01 --columns deps,wths --interval=quarter
  ```
  ```
                       ╷ 23q2           ╷ 23q1           ╷ 22q4          ╷ 22q3          ╷ 22q2          ╷ 22q1         ╷
  Account              │ Deps    Wths   │ Deps    Wths   │ Deps    Wths  │ Deps    Wths  │ Deps    Wths  │ Deps   Wths  │
  .*                   │ 52,462  -3,203 │ 44,122  -3,183 │ 32,743    -80 │ 24,596    -60 │ 16,313    -40 │ 8,194    -20 │
  saving:.*            │ 11,140  -3,203 │  9,550  -3,183 │  7,960    -80 │  5,620    -60 │  3,280    -40 │ 1,290    -20 │
  saving:regularSaver  │  3,000  -3,083 │  3,000  -3,083 │  3,000      0 │  2,250      0 │  1,500      0 │   750      0 │
  saving:regularSaver2 │  4,900       0 │  3,850       0 │  2,800      0 │  1,750      0 │    700      0 │     …      … │
  saving:savings       │  3,240    -120 │  2,700    -100 │  2,160    -80 │  1,620    -60 │  1,080    -40 │   540    -20 │
  trading:.*           │ 41,322       0 │ 34,572       0 │ 24,783      0 │ 18,976      0 │ 13,033      0 │ 6,904      0 │
  trading:index        │ 32,322       0 │ 27,072       0 │ 18,783      0 │ 14,476      0 │ 10,033      0 │ 5,404      0 │
  trading:risky        │  9,000       0 │  7,500       0 │  6,000      0 │  4,500      0 │  3,000      0 │ 1,500      0 │
  ```
- Print changes in deposits+withdrawals in each period for accounts and groups quarterly
  ```bash
  beanvest returns sample --end=2023-07-01 --columns dw --interval=quarter --delta
  ```
  ```
                       ╷ 23q2  ╷ 23q1   ╷ 22q4  ╷ 22q3  ╷ 22q2  ╷ 22q1  ╷
  Account              │ pDW   │ pDW    │ pDW   │ pDW   │ pDW   │ pDW   │
  .*                   │ 8,320 │  8,276 │ 8,127 │ 8,263 │ 8,099 │ 8,174 │
  saving:.*            │ 1,570 │ -1,513 │ 2,320 │ 2,320 │ 1,970 │ 1,270 │
  saving:regularSaver  │     0 │ -3,083 │   750 │   750 │   750 │   750 │
  saving:regularSaver2 │ 1,050 │  1,050 │ 1,050 │ 1,050 │   700 │     … │
  saving:savings       │   520 │    520 │   520 │   520 │   520 │   520 │
  trading:.*           │ 6,750 │  9,789 │ 5,807 │ 5,943 │ 6,129 │ 6,904 │
  trading:index        │ 5,250 │  8,289 │ 4,307 │ 4,443 │ 4,629 │ 5,404 │
  trading:risky        │ 1,500 │  1,500 │ 1,500 │ 1,500 │ 1,500 │ 1,500 │
  ```
