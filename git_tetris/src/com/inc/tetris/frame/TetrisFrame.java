package com.inc.tetris.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import com.inc.tetris.block.Block;
import com.inc.tetris.block.BlockTransformer;
import com.inc.tetris.block.KeepBlockStatus;
import com.inc.tetris.block.NextBlockStatus;
import com.inc.tetris.block.Shape;
import com.inc.tetris.cell.Cell;

public class TetrisFrame extends JFrame {
	private JPanel mainPanel;
	private JButton startBtn;
	private JLabel jumsooLabel; //점수
	private JTextField jumsooField; //점수필드
	
	private JLabel nextLabel; //다음블럭
	
	private JLabel keepLabel; //보관한 블럭
	private JTextArea helpArea;
	private Image backGround;
	
	
	//가로길이, 세로길이, 열, 행
	public static final int W = 510;
	public static final int H = 639;
	public static final int ROWS = 24;
	public static final int COLS = 10;
	
	
	
	//셀의 배열
	private Cell[][] cells = new Cell[ROWS][COLS];
	
	
	//현재의 블록
	public Block curBlock;
	//보관한 블록
	Block keepBlock;
		
	//블록의 위치를 바꾸는 클래스
	BlockTransformer transformer;

	NextBlockStatus nextStatus;
	KeepBlockStatus keepStatus;
	private boolean isRestart; //중지, 재시작시 사용
	private boolean gameCheck; //종료 후 재시작시 사용
	private int jumsoo; //점수
	//다운 스레드
	
	//게임 시작을 눌렀을 때 옴
	Timer downThread = new Timer(1000, new ActionListener() { //1초마다 실행
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(jumsoo <= 20) {
				process();
			}else if(jumsoo <= 40) {
				downThread.setInitialDelay(600);
				process();
			}else if(jumsoo <= 60) {
				downThread.setInitialDelay(400);
				process();
			}else {
				downThread.setInitialDelay(200);
				process();
			}
		}
	});
	
	private boolean process() {
		
		if(transformer.canDown()) { //true면 내릴 수 있음.(아직 닿지 않았음)
			transformer.down();
		}else {
			lineCheck(); //맨 밑줄부터 체크 가득차 있으면 점수, 라인제거			
			if(!isRestart) { //false이면
				nextStatus.nextBlock();
			}
			return false;
		}
		
		mainPanel.repaint();
		nextStatus.nextPanel.repaint();
		return true;
	}
	
	/*
	 * 각 라인이 가득 차 있는지
	 * 즉, 각 셀마다 블럭하나가 다 있는지
	 */
	private void lineCheck() {
		
		for(int row = ROWS-1; row >= 4; row--) {
			int count = 0;
			for(int col = 0; col < COLS; col++) {
				if(cells[row][col].isFixed()) { //각셀마다 블럭이  있다면
					blockChange();
					count++; //카운트 1 증가
				}
				if(gameOver(row,col)) {
					return;
				}
			}
			if(count == 10) {
				deleteLine(row); //다 채워진 열은 제거
				jumsoo++;
				jumsooField.setText("   "+jumsoo*10);
				row++;//비교를 다시 그 아랫줄부터 시작
			}
	
		}
	}
	
	private void deleteLine(int r) { //카운트가 10이되면 그때의 row값을 가져옴
		for(int row = r; row >= 4; row --){
			for(int col = 0; col < COLS; col ++) {
				//고정인지, 보여져있는지
				cells[row][col].setFixed(cells[row-1][col].isFixed()); //현재 고정해야 하는 값을 바로위에꺼로 변경
				cells[row][col].setVisible(cells[row-1][col].isVisible()); //그러면서 그 위에껄 보여지게
				cells[row][col].setC(cells[row-1][col].getC()); //색도 위에 있는 라인의 색을 가져옴
			}
		}
	}
	private boolean gameOver(int r, int c) {		
		if(cells[r][c].isFixed() && r <= 4) {			
			downThread.stop();
			startBtn.setText("Start");
			JOptionPane.showMessageDialog(null, "게임종료\n"+(jumsoo*10)+"점 입니다.");
			for(int row = 0; row<ROWS; row++) {
				for(int col = 0; col<COLS; col++) {
					cells[row][col].setFixed(false);
					cells[row][col].setVisible(false);
				}
			}
			jumsoo = 0;
			jumsooField.setText("   "+jumsoo);
			mainPanel.repaint();
			gameCheck = true;
		}
		return gameCheck;
	}

	public void blockChange() {
		curBlock.blockPoint.y = nextStatus.nextBlock.blockPoint.y - 2;
		curBlock.blockPoint.x = 0;

		curBlock.setShape(nextStatus.nextBlock.getShape());
		curBlock.setShapeType(nextStatus.nextBlock.getShapeType());
		curBlock.setRotation(nextStatus.nextBlock.getRotation());
		curBlock.setC(nextStatus.nextBlock.getC());

		for (int row = 0; row < NextBlockStatus.NEXTROWS; row++) {
			for (int col = 0; col < NextBlockStatus.NEXTCOLS; col++) {
				nextStatus.nextcells[row][col].setVisible(false);
			}
		}
		nextStatus.nextPanel.repaint();
	}
	
	public TetrisFrame() {
		setTitle("테트리스");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 50, W, H);
		setResizable(false);
		
		init();
		
		setVisible(true); 
	}
	
	private void init() {
		initCells(); //셀을통해 2중포문으로 배열 채우기 (24x10)
		nextStatus = new NextBlockStatus();
		keepStatus = new KeepBlockStatus();
		nextStatus.nextCells();
		keepStatus.keepCells();
		initComponent();
		initPanel();
		initEvent();
		initKeyEvent();
		keepEvent();
	}
	
	//row, col을 셀의 수만큼 반복하면서
	/*화면에 보여지는 칸을 회색으로
	 * 
	 */
	private void initCells() { //
		for(int row = 0; row < ROWS; row ++) { 
			for(int col = 0; col < COLS; col++) { 
				cells[row][col] = new Cell(
						new Point(col * Cell.CELL_SZIE,(row -4) * Cell.CELL_SZIE ), 
							Color.RED); //보이진 않지만 칸을 만들기
			}
		}
	}
	
	private void initComponent() {
		startBtn = new JButton("Start");
		startBtn.setBounds(320, 550, 140, 40); //절대좌표
		jumsooLabel = new JLabel("점수 : ");
		jumsooLabel.setBounds(320, 500, 60, 40);
		jumsooField = new JTextField(4);
		jumsooField.setBounds(370, 505, 60, 30);
		jumsooField.setEditable(false);
		nextLabel = new JLabel("다음 블럭");
		nextLabel.setBounds(375,5,60,30);
		keepLabel = new JLabel("보관한 블럭");
		keepLabel.setBounds(370, 200, 70, 30);
		helpArea = new JTextArea();
		helpArea.setEditable(false);
		helpArea.append("          - 도움말 - \n 블록이동 : ←, →, ↓\n 블록 방향변경 : ↑\n 블록보관 : 엔터\n 보관한블록 가져오기 : 시프트");
		helpArea.setBounds(310, 380, 170, 140);
		//(340,240,160,130);
		/*
		 * BloackTransformer로 Cell의 포인트와 색을 변경
		 * 블럭을 움직였을 때, 옮기기 전의 셀의 색을 회색으로, 셀은 안보이게
		 */
		transformer = new BlockTransformer(cells);
	}
	
	private void initPanel() {
		nextStatus.nextPanel();
		keepStatus.keepPanel();
		ImageIcon backIcon = new ImageIcon("src/images/프리지아.png");//
		backGround = backIcon.getImage();//		
		mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D)g;
				g2.clearRect(0, 0, W, H); //안하게되면 블럭들의 잔상이 남아있음.
				g2.drawImage(backGround, 0, 0, this); //
				//보여지는 공간만 반복
				for(int row = 4; row<ROWS; row++) {
					for(int col = 0; col<COLS; col++) {
						Cell cell = cells[row][col]; //cell을 새로 생성해서 그려야 할 곳 지정
						/*cell에 row와 col에 해당하는 좌표를 넣음
						 */
							if(cell.isVisible()) { //true라면 해당 포인트만 색칠 //게임이 시작되기 전에는 false
								g2.setColor(cell.getC());  // 도형의 색(Shape 클래스에서 가져옴)
								g2.fillRect(cell.p.x, cell.p.y, Cell.CELL_SZIE, Cell.CELL_SZIE);
							}
							g2.setColor(Color.GRAY); //cell의 색상(선의 색)
							g2.drawRect(cell.p.x, cell.p.y, Cell.CELL_SZIE, Cell.CELL_SZIE); //하나의 셀의 크기(모양을 만듬)
//							g2.drawImage(backGround, cell.p.x, cell.p.y, this);
					}
				}
			}
		};
		mainPanel.setLayout(null); //레이아웃을 지정안하고 setBounds로 위치지정
		nextStatus.nextPanel.setLayout(null);
		mainPanel.add(startBtn);
		mainPanel.add(jumsooLabel);
		mainPanel.add(jumsooField);
		mainPanel.add(nextLabel);
		mainPanel.add(nextStatus.nextPanel);
		mainPanel.add(keepLabel);
		mainPanel.add(keepStatus.keepPanel);
		mainPanel.add(helpArea);
		
		
		add(mainPanel);
	}
	
	private void initEvent() {
		startBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String text = ((JButton)e.getComponent()).getText();
					if(text.equals("Pause")) {
						gamePause();
					}else if(text.equals("Restart")){
						gameRestart();
					}else{
						startBtn.setText("Pause");
						gameStart();
					}
			}
		});
	}
	//isRestart가 true면 멈추면서 새로운 블럭생성 x
	private void gamePause() {
		isRestart = true;
		startBtn.setText("Restart");						
		downThread.stop();
	}
	private void gameRestart() {
		isRestart = true;
		downThread.restart();
		requestFocus();
		startBtn.setText("Pause");
		isRestart = false;
		process();
	}
	
	private void keepEvent() {
		addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					blockKeep(e.getKeyCode());
					blockStore(e.getKeyCode());
				}
		});
	}
	

	private void blockStore(int keyCode) {
		if(keyCode == KeyEvent.VK_SHIFT) {
			
			//현재 보관해놓은 블럭 고정을 해제		
			for(Point p : keepBlock.getShape()) {
				if(keepStatus.keepcells[p.x][p.y].isFixed()) {
					keepStatus.keepcells[p.x][p.y].setFixed(false);
				}
			}
			//보여지는 블럭을 안보이게 설정
			for(Point p : curBlock.getShape()) {
					cells[p.x][p.y].setVisible(false);
			}
			
			//보관해놓은 블럭을 옮기기 위한 변수
			curBlock.blockPoint.y = 0;
			curBlock.blockPoint.x = 0;
			curBlock.setShape(keepBlock.getShape());
			curBlock.setShapeType(keepBlock.getShapeType());
			curBlock.setRotation(keepBlock.getRotation());
			curBlock.setC(keepBlock.getC());
			
			//보관해놓은 블럭의 주소값을 받은 블록을 보여지게하면서 색깔 지정
			for(Point p : curBlock.getShape()) {
				cells[p.x][p.y].setVisible(true);
				cells[p.x][p.y].setC(keepBlock.getC());
			}
			//보관해놓았던 블럭을 안보이게 설정
			for(int row = 0; row < KeepBlockStatus.KEEPROWS; row++) {
				for(int col = 0; col < KeepBlockStatus.KEEPCOLS; col++) {
					keepStatus.keepcells[row][col].setVisible(false);	
				}
			}
			//보관해놓은 블럭을 보이게 해야 하므로 블럭이 안보이게
			for(Point p: curBlock.getShape()) {
				cells[p.x][p.y].setVisible(false);
			}
			
			keepStatus.keepPanel.repaint();
			mainPanel.repaint();
		}
	}
	
	private void blockKeep(int keyCode) {
		
		if(keyCode == KeyEvent.VK_ENTER) {
			keepBlock = new Block(curBlock.getShapeType(),curBlock.getC(),curBlock.getRotation());
			for(Point p : keepBlock.getShape()) {
				if(keepStatus.keepcells[p.x][p.y].isFixed()) {
					return;
				}
			}
			
			for(Point p : keepBlock.getShape()) {
				keepStatus.keepcells[p.x][p.y].setVisible(true);
				keepStatus.keepcells[p.x][p.y].setC(curBlock.getC());
				keepStatus.keepcells[p.x][p.y].setFixed(true);
			}
			
			keepStatus.keepPanel.repaint();
			for(Point p : curBlock.getShape()) {
				cells[p.x][p.y].setVisible(false);
			}
			
			blockChange();
			
			nextStatus.nextBlock();
		}
		
	}
	
	private void initKeyEvent() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DOWN) { //DOWN키를 누르면 process가 실행이 되도록
					process();
					return;
				}else if(e.getKeyCode() == KeyEvent.VK_SPACE) { //스페이스를 누를때
					//process가 true라는건 더이상 내릴 곳이 없다는 뜻 없을때까지 내리기
					while(process()) {}
						return;
				}
				transformer.move(e.getKeyCode());				
				mainPanel.repaint();
			}
		});
	}
	
	private void gameStart() { //시작 버튼을 눌렀을 때
		if(gameCheck) {
			gameCheck = false;
		}
		for(int row = 0; row < NextBlockStatus.NEXTROWS; row++) {
			for(int col = 0; col < NextBlockStatus.NEXTCOLS; col++) {
				nextStatus.nextcells[row][col].setVisible(false);	
			}
		}
		addBlock();
		nextStatus.nextBlock();
		downThread.start();
		requestFocus(); //이벤트의 포커스를 옮김 버튼 -> 화면으로
		
	}
	/*
	 * //여기까지 하면 그 위치에 블럭이 보이고, 색이 보임
	 * 먼저 블럭이 안보이는 공간에 생성이 되면서 어떤블럭이며 색이 어떤것인지 지정
	 * 
	 */
	private void addBlock() {  
		//모양이 총 7개 있기때문에, 그 모양중 어떤 모양을 생성할 지 랜덤으로 지정
		int ranNum = (int)(Math.random() * Shape.SHAPE.length); // 0부터 6사이의 난수
		//현재 블록의 모양과 색 지정
		curBlock = new Block(ranNum, Shape.COLOR[ranNum]); 
		//transformer에 위에서 만든 curBlock을 넣어서 만들어줌
		transformer.setBlock(curBlock); //BlockTransformer에 현재의 블록을 넘겨줌
		//블록의 위치를 바꾸는건 BlockTransformer가 하도록
		
		for(Point p : curBlock.getShape()) { //블럭의 모양을 가져옴
			p.y += 2; //그 위치에서 y값을 2증가
			cells[p.x][p.y].setVisible(true); //(해당되는 셀만 보이게 해라)
			cells[p.x][p.y].setC(curBlock.getC()); //해당 셀의 색을 바꿔줌
		} 
		mainPanel.repaint();
	}
	
	public static void main(String[] args) {
		new TetrisFrame();
	}
	
	
}
