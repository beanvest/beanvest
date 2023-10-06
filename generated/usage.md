## Usage examples
Journals used to generate these reports are in `sample/` directory.

- Print gains and returns of each year
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns again,xirr --interval=year --startDate=2019-01-01 --delta
  ```
  ```
                       ╷ 2022          ╷ 2021          ╷ 2020          ╷ 2019          ╷
  Account              │ pAGain  pXirr │ pAGain  pXirr │ pAGain  pXirr │ pAGain  pXirr │
  .*                   │  9,065    7.5 │  5,614    6.7 │  4,415    9.0 │  1,827   11.2 │
  saving:.*            │    199    3.0 │    170    4.0 │     83    5.2 │      …      … │
  saving:regularSaver  │      0      … │    101    5.2 │     83    5.2 │      …      … │
  saving:regularSaver2 │    199    3.0 │     69    3.0 │      …      … │      …      … │
  trading:.*           │  8,866    7.8 │  5,444    6.8 │  4,332    9.1 │  1,827   11.2 │
  trading:index        │  8,550    9.2 │  6,466   10.1 │  4,227   11.1 │  1,584   12.1 │
  trading:risky        │    316    1.5 │ -1,021   -6.5 │    105    1.1 │    243    7.6 │
  ```
- Print cumulative gains and total return of trading accounts after each year
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns again,xirr --interval=year --startDate=2019-01-01
  ```
  ```
                       ╷ 2022          ╷ 2021          ╷ 2020         ╷ 2019         ╷
  Account              │ AGain   Xirr  │ AGain   Xirr  │ AGain  Xirr  │ AGain  Xirr  │
  .*                   │ 20,920    7.8 │ 11,856    8.0 │ 6,242    9.6 │ 1,827   11.2 │
  saving:.*            │    451    3.7 │    253    4.4 │    83    5.2 │     …      … │
  saving:regularSaver  │    184    5.2 │    184    5.2 │    83    5.2 │     …      … │
  saving:regularSaver2 │    268    3.0 │     69    3.0 │     …      … │     …      … │
  trading:.*           │ 20,469    8.0 │ 11,603    8.2 │ 6,159    9.7 │ 1,827   11.2 │
  trading:index        │ 20,826   10.0 │ 12,276   10.7 │ 5,811   11.4 │ 1,584   12.1 │
  trading:risky        │   -357   -0.7 │   -673   -2.5 │   348    2.8 │   243    7.6 │
  ```
- Print various stats for all accounts and groups on each level of the accounts
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns Deps,Wths,Div,Intr,Fees,Value,rgain,ugain
  ```
  ```
  Account               Deps     Wths     Div    Intr   Fees   Value    RGain  UGain
  .*                    154,127  -13,351  1,108    451    -81  167,308      0  24,973
  saving:.*              12,900  -13,351      0    451      0        0      0       0
  saving:regularSaver     4,500   -4,684      0    184      0        0      0       0
  saving:regularSaver2    8,400   -8,668      0    268      0        0      0       0
  trading:.*            141,227        0  1,108      0    -81  167,308      0  24,973
  trading:index         113,727        0      0      0      0  139,870      0  26,143
  trading:risky          27,500        0  1,108      0    -81   27,438      0  -1,170
  ```
- Print cash stats on holdings, accounts and groups
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns Deps,Wths,Value,again,xirr --report-holdings
  ```
  ```
  Account                   Deps     Wths     Value    AGain   Xirr
  .*                        154,127  -13,351  167,308  26,532    7.7
  saving:.*                  12,900  -13,351        0     451    3.7
  saving:regularSaver         4,500   -4,684        0     184    5.2
  saving:regularSaver2        8,400   -8,668        0     268    3.0
  saving:regularSaver2:GBP    8,400   -8,668        0     268      …
  saving:regularSaver:GBP     4,500   -4,684        0     184      …
  trading:.*                141,227        0  167,308  26,081    7.9
  trading:index             113,727        0  139,870  26,143    9.7
  trading:index:GBP         113,727        0    2,068       0      …
  trading:index:SPX               0        0  137,802  26,143    9.8
  trading:risky              27,500        0   27,438     -62   -0.1
  trading:risky:GBP          27,500        0    1,608       0      …
  trading:risky:RSK               0        0   25,830     -62   -0.1
  ```
- Print value of the accounts and total gains quarterly
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns value,again --interval=quarter --startDate=2022-07-01
  ```
  ```
                       ╷ 23q2            ╷ 23q1            ╷ 22q4            ╷ 22q3            ╷
  Account              │ Value    AGain  │ Value    AGain  │ Value    AGain  │ Value    AGain  │
  .*                   │ 164,740  26,532 │ 154,269  23,664 │ 143,828  20,920 │ 133,602  18,367 │
  saving:.*            │       0     451 │       0     451 │   8,668     451 │   7,556     389 │
  saving:regularSaver  │       0     184 │       0     184 │       0     184 │       0     184 │
  saving:regularSaver2 │       0     268 │       0     268 │   8,668     268 │   7,556     206 │
  trading:.*           │ 164,740  26,081 │ 154,269  23,213 │ 135,161  20,469 │ 126,047  17,978 │
  trading:index        │ 137,802  26,143 │ 128,944  23,388 │ 111,518  20,826 │ 104,097  18,528 │
  trading:risky        │  26,938     -62 │  25,325    -175 │  23,643    -357 │  21,950    -550 │
  ```
- Print monthly net deposits (deposits-withdrawals)
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns dw --interval=month --startDate=2023-01-01 --delta
  ```
  ```
                       ╷ 23m06 ╷ 23m05 ╷ 23m04 ╷ 23m03 ╷ 23m02 ╷ 23m01  ╷
  Account              │ pDW   │ pDW   │ pDW   │ pDW   │ pDW   │ pDW    │
  .*                   │ 2,509 │ 2,560 │ 2,534 │ 2,517 │ 2,588 │  2,591 │
  saving:.*            │     0 │     0 │     0 │     0 │     0 │ -8,668 │
  saving:regularSaver  │     0 │     0 │     0 │     0 │     0 │      0 │
  saving:regularSaver2 │     0 │     0 │     0 │     0 │     0 │ -8,668 │
  trading:.*           │ 2,509 │ 2,560 │ 2,534 │ 2,517 │ 2,588 │ 11,259 │
  trading:index        │ 2,009 │ 2,060 │ 2,034 │ 2,017 │ 2,088 │ 10,759 │
  trading:risky        │   500 │   500 │   500 │   500 │   500 │    500 │
  ```
- Print monthly net deposits and changes in value converted to other currency
  ```bash
  beanvest returns sample/ --end=2023-07-01 --columns dw,value --interval=month --startDate=2023-01-01 --delta --currency PLN
  ```
  ```
                       ╷ 23m06          ╷ 23m05          ╷ 23m04          ╷ 23m03          ╷ 23m02          ╷ 23m01            ╷
  Account              │ pDW     pValue │ pDW     pValue │ pDW     pValue │ pDW     pValue │ pDW     pValue │ pDW      pValue  │
  .*                   │ 12,696  17,122 │ 12,954  17,460 │ 12,822  18,402 │ 12,761  14,332 │ 13,095  20,575 │ 101,443  105,638 │
  saving:.*            │      0       0 │      0       0 │      0       0 │      0    -267 │      0     267 │  44,473   43,858 │
  saving:regularSaver  │      0       0 │      0       0 │      0       0 │      0     -94 │      0      94 │       0        0 │
  saving:regularSaver2 │      0       0 │      0       0 │      0       0 │      0    -173 │      0     173 │  44,473   43,858 │
  trading:.*           │ 12,696  17,122 │ 12,954  17,460 │ 12,822  18,402 │ 12,761  14,599 │ 13,095  20,308 │  56,971   61,780 │
  trading:index        │ 10,166  14,928 │ 10,424  14,977 │ 10,292  14,916 │ 10,226  13,047 │ 10,565  16,348 │  54,441   58,778 │
  trading:risky        │  2,530   2,194 │  2,530   2,483 │  2,530   3,486 │  2,535   1,552 │  2,530   3,960 │   2,530    3,002 │
  ```
