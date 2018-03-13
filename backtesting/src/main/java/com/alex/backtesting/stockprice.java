package com.alex.backtesting;

public class stockprice {
	String Date = "";
	String Code = "";
	double Open = 0;
	double High = 0;
	double Low = 0;
	double Close = 0;
	double Adj_close = 0;
	int Volume = 0;
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public double getOpen() {
		return Open;
	}
	public void setOpen(double open) {
		Open = open;
	}
	public double getHigh() {
		return High;
	}
	public void setHigh(double high) {
		High = high;
	}
	public double getLow() {
		return Low;
	}
	public void setLow(double low) {
		Low = low;
	}
	public double getClose() {
		return Close;
	}
	public void setClose(double close) {
		Close = close;
	}
	public double getAdj_close() {
		return Adj_close;
	}
	public void setAdj_close(double adj_close) {
		Adj_close = adj_close;
	}
	public int getVolume() {
		return Volume;
	}
	public void setVolume(int vloume) {
		Volume = vloume;
	}
	
	
	

}
