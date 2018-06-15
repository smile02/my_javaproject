package com.inc.tetris.cell;

import java.awt.Color;
import java.awt.Point;

public class Cell {
	//셀의 위치 x,y좌표 ㅁ << 이렇게 생긴애 각 셀(모눈)
	public Point p;
	
	//셀의 크기 셀의 가로,세로 길이는 30
	public static final int CELL_SZIE = 30;
	
	//셀의 색상
	private Color c;
	
	//보여져야 하는지 여부
	private boolean isVisible;
	
	//쌓여져 있는지 여부
	private boolean isFixed;
	
	//생성자로 좌표와 색상을 받아옴
	public Cell(Point p, Color c) {
		this.p = p; 
		this.c = c;
				
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}
	
	
	
}
