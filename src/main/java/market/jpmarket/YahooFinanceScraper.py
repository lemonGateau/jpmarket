# coding utf-8
import sys
import time
import json
import pandas as pd
import yfinance as yf

def fetch_ohlcv(stock_code):
    if ".T" not in stock_code:
        stock_code += ".T"

    df = yf.download(stock_code, period="1y", interval="1d")
    df = df.reset_index()

    if "Datetime" in df.columns:
        df["Datetime"] = df["Datetime"].dt.tz_convert('Asia/Tokyo')
        df = df.set_index("Datetime")
    else:
        df = df.set_index("Date")
    
    df.columns = ["Open", "High", "Low", "Close", "Volume"]
    
    df = df.round(1) 

    return df

# Todo
def fetch_fundamentals(stock_code):
    if ".T" not in stock_code:
        stock_code += ".T"

    stock_info = yf.Ticker(stock_code).info

    df = pd.DataFrame([stock_info])

    # print(json.dumps(stock_info, indent=4))

    df = df[["shortName", "trailingPE", "priceToBook", "dividendYield", "dividendRate", "returnOnEquity"]]
    df = df.set_index("shortName")

    df = df.round(2)

    return df

if __name__ == '__main__':
    stock_code = sys.argv[1]

    df_ohlcv = fetch_ohlcv(stock_code)
    print(df_ohlcv)

    df_fundamentals = fetch_fundamentals(stock_code)
    print(df_fundamentals)

    save_folder = "C:\\pleiades\\2024-09\\workspace\\jpmarket\\csv\\"

    path_ohlcv        = save_folder + stock_code + "_ohlcv.csv"
    path_fundamentals = save_folder + stock_code + "_fundamentals.csv"

    df_ohlcv.to_csv(path_ohlcv, header=True, index=True)
    df_fundamentals.to_csv(path_fundamentals, header=True, index=True)
