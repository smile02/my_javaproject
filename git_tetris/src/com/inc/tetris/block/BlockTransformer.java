package com.inc.tetris.block;

import java.awt.Point;
import java.awt.event.KeyEvent;

import com.inc.tetris.cell.Cell;
import com.inc.tetris.frame.TetrisFrame;

public class BlockTransformer {
	//블록, 셀에서 주소값을 받아옴(new해서 생성하지 않아서 주소값이 같음)
	private Block curBlock;
	
	private Cell[][] cells;
	
	public BlockTransformer(Cell[][] cells) {
		this.cells = cells; //처음에 생성할때만 받는 값 한번 보낸게 바뀌지않음
	}
	
	public void setBlock(Block block) {
		this.curBlock = block; //수시로 바뀜 그래서 생성자에서 초기화 x
	}
	
	private void fix() {
		for(Point p : curBlock.getShape()) {
			cells[p.x][p.y].setFixed(true);
		}
	}
	
	/*
	 * 현재 생성된 블럭의 모양을 가져와서
	 * 좌표의 수 즉, 1의 갯수만큼 반복하면서
	 * 숫자가 1인 셀이 바닥에 닿았거나 그거보다 크면 고정
	 * fix()메서드에서 현재 좌표에 있는 cell들을 고정시킴
	 */
	public boolean canDown() { //바닥에 닿았는지 아닌지를 판단
		for(Point p : curBlock.getShape()) {
			if(p.x >= TetrisFrame.ROWS -1 ) { //바닥에 닿았음
				fix();
				return false;
			}
			if(cells[p.x+1][p.y].isFixed()) {
				fix();
				return false;
			}
		}
			
		return true;
	}
	
	public void move(int keyCode) {
		
		switch(keyCode) {
			case KeyEvent.VK_LEFT: moveLeft(); break;
			case KeyEvent.VK_RIGHT: moveRight(); break;
			case KeyEvent.VK_UP: rotate(); break;

		}
	}
	private void rotate() {
		int rotation = curBlock.getRotation(); //현재 블록의 회전 수를 가져옴
		if(rotation == 3) { //회전 수가 3이면 0으로
			rotation = 0;
		}else {
			rotation++; 
		}
		Block tmpBlock = new Block(curBlock.getShapeType(), curBlock.getC(),rotation); //블록의 모양, 색, 회전수를 보냄
		//좌, 우, 아래를 누를때마다 좌표를 받아옴
		//블럭에 있는 좌표를 가져와서 임시의 블럭에 현재 좌표값을 넣어줌
		tmpBlock.blockPoint.y  = curBlock.blockPoint.y; 
	 	tmpBlock.blockPoint.x  = curBlock.blockPoint.x;
	 	
		//임시블럭에 위치를 p.x, p.y에 넣어주기
	 	//1에 해당하는 수를 넣어줌
		for(Point p : tmpBlock.getShape()) {
			p.x += tmpBlock.blockPoint.x;
			p.y += tmpBlock.blockPoint.y;
		}
		
		//돌릴 수 있는지 검사
		for(Point p : tmpBlock.getShape()) {
			//각 면에 닿았을 때 못 돌리게
			if(p.x > 19 || p.y <0 || p.y > 9 || cells[p.x][p.y].isFixed()) {
				return; //아예 못돌리게 리턴
			}
		}
		
			//감추기
			hide();
			//curBlock에 모양과 회전을 임시 블럭에 만들었던거로 지정
			//돌리기 
			curBlock.setShape(tmpBlock.getShape());
			curBlock.setRotation(tmpBlock.getRotation());
			//위에서 만든 블럭에서 1에 해당하는 값만큼 반복을 돌면서
			//그 좌표에 있는 블럭을 보이게하면서 색도 보이게
			//보여주기
			for(Point p : curBlock.getShape()) {
				cells[p.x][p.y].setVisible(true);
				cells[p.x][p.y].setC(curBlock.getC());
			}
	}
	
	private void moveRight() {
		for(Point p : curBlock.getShape()) {
			if(p.y == TetrisFrame.COLS -1 || cells[p.x][p.y + 1].isFixed()) { //범위를 벗어나거나 오른쪽에 고정되어 있는 블록이 있으면 리턴
				return ; //아래가 실행이 안되게
			}
		}
		hide();
		for(Point p : curBlock.getShape()) {
			p.y++; //현재 움직이는 블럭의 위치			
			cells[p.x][p.y].setVisible(true);
			cells[p.x][p.y].setC(curBlock.getC());
		}
		curBlock.blockPoint.y++; //가상의 블럭의 위치(회전하기 위해서) 키를 누를때마다 가상의 블럭의 위치를 조정
	}

	private void moveLeft() {
		for(Point p : curBlock.getShape()) { //범위를 벗어나거나 왼쪽에 고정되어 있는 블록이 있으면 리턴
			if(p.y == 0 || cells[p.x][p.y -1].isFixed()) {
				return ; //아래가 실행이 안되게
			}
		}
		hide();
		for(Point p : curBlock.getShape()) {
			p.y--;			
			cells[p.x][p.y].setVisible(true);
			cells[p.x][p.y].setC(curBlock.getC());
		}
		curBlock.blockPoint.y--; //
	}
	/*
	 * canDown이 true일때 들어옴
	 * 현재 생성된 블럭이 내려오면서 바닥에 닿지않거나
	 * 더 아래에 있는 블럭에 닿지 않았을 때
	 * 아래화살표를 누를때마다 좌표1씩증가하면서 보이게하면서 색을 가져옴
	 */
	public void down() {
		hide(); //내렸을 때 기존에 있던 셀들은 안보이게 설정
		for(Point p : curBlock.getShape()) {			
			p.x++;			
			cells[p.x][p.y].setVisible(true);
			cells[p.x][p.y].setC(curBlock.getC());
		}
		curBlock.blockPoint.x++; //가상의 셀 위치를 1씩 증가함
	}
	//감추기(기존의 블럭)
	private void hide() {

		for(Point p : curBlock.getShape()) {
				cells[p.x][p.y].setVisible(false);				
			}
		
		
		}
	
}


