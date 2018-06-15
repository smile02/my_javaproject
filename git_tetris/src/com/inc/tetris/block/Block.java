package com.inc.tetris.block;

import java.awt.Color;
import java.awt.Point;

public class Block {
	private Point[] shape; //각 블록들의 배열
	private int shapeType; //블록의 모양 길쭉이인지 네모인지
	private int rotation; //회전의 수를 저장해줄 변수
	public Point blockPoint; // 칠해져 있지 않은 셀
	private Color c;
	
	public Block(int shapeType, Color c) {
		blockPoint = new Point(0,2); //처음 위치 
		this.shapeType = shapeType;
		this.c = c;
		
		initShape();
	}
	
	public Block(int shapeType, Color c, int rotation) {
		blockPoint = new Point(0,2);
		this.shapeType = shapeType;
		this.rotation = rotation;
		this.c = c;
		
		initShape();
	}

	//밸류 오브젝트 ( 값을 가지고 있는 객체)
	private void initShape() {
		shape = new Point[4];
		//현재 블럭들의 각 좌표를 알 수 있다.
		int i = 0;
		
		for(int row = 0; row < 4; row++) {
			for(int col = 0; col < 4; col++) {
				//4차원 배열에서 ex) shapeType, rotation이 0이면
				//0,0,0,0에 1이라는 수가 있는지
				//만약에 있다면 shape[i]번째에 row,col 즉 각 위치를 Point로 넣어줌
				if(Shape.SHAPE[shapeType][rotation][row][col] == 1) {
					shape[i] = new Point(row,col);
					i++;
					if(i>4) {
						break;
					}
				}
			}
		}
	}

	
	public Point[] getShape() {
		return shape;
	}

	public void setShape(Point[] shape) {
		this.shape = shape;
	}

	public int getShapeType() {
		return shapeType;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

	
}
