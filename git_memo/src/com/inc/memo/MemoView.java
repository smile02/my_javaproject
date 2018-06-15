package com.inc.memo;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MemoView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2638150202403546708L;

	private CardLayout cl;

	// 페널
	private JPanel menuPanel;
	private JPanel viewPanel;
	private JPanel titlePanel;
	private JPanel contentPanel;
	private JPanel wTextPanel; // 글을쓰는 패널(기본값)
	private JPanel wDrawPanel;
	private JPanel wTextReadPanel;
//	private JPanel wDrawReadPanel;

	private JTextField titleField;
	private JTextArea contentArea;
	private JTextArea readArea;

	// 레이블
	private JLabel titleLabel;
	private JLabel imageLabel;
//	private JLabel readLabel;

	// 메뉴바
	private JMenuBar menubar;
	// 메뉴
	/*
	 * 메모쓰기(글,그림) / 읽기(선택,저장) / 저장 / 음악재생
	 */
	private JMenu write;
	private JMenu read;
	private JMenu stroke;
	private JMenu color;

	// 글쓰기, 그림그리기
	private JMenuItem writeText;
	private JMenuItem writeDraw;

	// 선택읽기, 전체읽기
	private JMenuItem readAll;

	// 굵기
	private JMenuItem stroke1;
	private JMenuItem stroke3;
	private JMenuItem stroke5;
	private JMenuItem stroke7;
	private JMenuItem stroke9;

	// 색상
	private JMenuItem red;
	private JMenuItem blue;
	private JMenuItem green;
	private JMenuItem black;
	private JMenuItem orange;

	private JButton save;

	private int check;

	private Color c;

	private int strokeSize = 0;

	private Point startPoint;
	private Point endPoint;

	FileDialog dialog;

	String directory;
	String fileName;

	int count;
	int open;
	BufferedImage canvasImage;

	public MemoView() {
		setTitle("메모장");
		setDefaultCloseOperation(EXIT_ON_CLOSE); // 프로그램 종료
		setBounds(300, 300, 500, 500); // 전체크기
		setResizable(false);
		cl = new CardLayout();
		init();

		setVisible(true); // 프로그램 실행되게
	}

	public void init() { // 초기화
		setImage(new BufferedImage(500, 500, BufferedImage.TYPE_4BYTE_ABGR));
		initComponent();
		initPanel();
		initEvent();
	}

	private void initPanel() {
		menuPanel = new JPanel();
		menuPanel.add(menubar);

		viewPanel = new JPanel(new BorderLayout()); // 제목+내용을 보여주는 패널
		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 제목
		wTextReadPanel = new JPanel(new BorderLayout());
		viewPanel.add(titlePanel, BorderLayout.NORTH);

		contentPanel = new JPanel(); // 내용부분을 보여주는 패널
		contentPanel.setLayout(cl); // 카드레이아웃으로 지정
		wDrawPanel = new JPanel(new BorderLayout());

		wTextPanel = new JPanel(new BorderLayout()); // 글쓰는 패널
		viewPanel.add(contentPanel);
		wTextReadPanel.add(new JScrollPane(readArea));
		
		contentPanel.add(new JScrollPane(wTextPanel), "text");
		contentPanel.add(wDrawPanel, "draw");
		contentPanel.add(wTextReadPanel, "textread");

		titlePanel.add(titleLabel);
		titlePanel.add(titleField);
		titlePanel.add(save);
		wTextPanel.add(contentArea);

		wDrawPanel.add(imageLabel);
		add(menuPanel, BorderLayout.NORTH);
		add(viewPanel);

	}

	public void initComponent() { // 초기화 한 값 작성
		titleField = new JTextField();
		contentArea = new JTextArea("내용없음");
		contentArea.setLineWrap(true);
		titleField.setPreferredSize(new Dimension(340, 20));
		readArea = new JTextArea();
		readArea.setEditable(false);
		readArea.setLineWrap(true);
		
		menubar = new JMenuBar();
		menubar.setPreferredSize(new Dimension(480, 20));

		write = new JMenu("메모 쓰기");
		read = new JMenu("메모 불러오기");
		stroke = new JMenu("굵기");
		color = new JMenu("색상");

		writeText = new JMenuItem("글로 메모");
		writeDraw = new JMenuItem("그림으로 메모");

		readAll = new JMenuItem("불러오기");

		stroke1 = new JMenuItem("1");
		stroke3 = new JMenuItem("3");
		stroke5 = new JMenuItem("5");
		stroke7 = new JMenuItem("7");
		stroke9 = new JMenuItem("9");

		red = new JMenuItem("빨강");
		green = new JMenuItem("초록");
		blue = new JMenuItem("파랑");
		black = new JMenuItem("검정");
		orange = new JMenuItem("주황");

		save = new JButton("저장하기");

		titleLabel = new JLabel("제목 : ");
		imageLabel = new JLabel(new ImageIcon(canvasImage));
		endPoint = new Point(-10, -10);
		write.add(writeText);
		write.add(writeDraw);

		read.add(readAll);

		stroke.add(stroke1);
		stroke.add(stroke3);
		stroke.add(stroke5);
		stroke.add(stroke7);
		stroke.add(stroke9);

		color.add(red);
		color.add(green);
		color.add(blue);
		color.add(black);
		color.add(orange);

		menubar.add(write);
		menubar.add(read);
		menubar.add(save);
		menubar.add(stroke);
		menubar.add(color);

	}

	private void initEvent() {
		MouseListener ml = new MouseAdapter() { // 메모쓰기인지, 읽기인지
			@Override
			public void mousePressed(MouseEvent e) {
				String text = ((JMenuItem) e.getComponent()).getText();
				switch (text) {
				case "글로 메모":
					cl.show(contentPanel, "text");
					titleField.setText(null);
					check = 0;
					contentArea.setText("내용없음");
					break;
				case "그림으로 메모":
					titleField.setText(null);
					check = 1;
					writeDraw();
					cl.show(contentPanel, "draw");
					break;
				case "불러오기":
					readMemo();
					if(open == 2) {
						cl.show(contentPanel, "textread");
					}else {
						cl.show(contentPanel, "draw");
					}
					break;
				}
			}
		};

		writeText.addMouseListener(ml);
		writeDraw.addMouseListener(ml);
		readAll.addMouseListener(ml);

		titleField.addMouseListener(new MouseAdapter() { // 텍스트필드 클릭 시 널
			@Override
			public void mouseReleased(MouseEvent e) {
				titleField.setText(null);
			}
		});

		contentArea.addMouseListener(new MouseAdapter() { // 에어리어 클릭 시 널
			@Override
			public void mouseReleased(MouseEvent e) {
				contentArea.setText(null);
			}
		});

		save.addMouseListener(new MouseAdapter() { // 저장하기를 눌렀을 때

			@Override
			public void mousePressed(MouseEvent e) {

				saveMemo();
			}
		});

		MouseListener colorM = new MouseAdapter() { // 붓 색 지정
			@Override
			public void mouseReleased(MouseEvent e) {
				String text = ((JMenuItem) e.getComponent()).getText();

				switch (text) {
				case "빨강":
					c = Color.RED;
					break;
				case "파랑":
					c = Color.BLUE;
					break;
				case "초록":
					c = Color.GREEN;
					break;
				case "검정":
					c = Color.BLACK;
					break;
				case "주황":
					c = Color.ORANGE;
					break;
				}
			}
		};
		red.addMouseListener(colorM);
		blue.addMouseListener(colorM);
		black.addMouseListener(colorM);
		green.addMouseListener(colorM);
		orange.addMouseListener(colorM);

		MouseListener sizeListener = new MouseAdapter() { // 선의 굵기
			@Override
			public void mousePressed(MouseEvent e) {
				String str = ((JMenuItem) e.getComponent()).getText();
				strokeSize = Integer.parseInt(str);
			}
		};
		stroke1.addMouseListener(sizeListener);
		stroke3.addMouseListener(sizeListener);
		stroke5.addMouseListener(sizeListener);
		stroke7.addMouseListener(sizeListener);
		stroke9.addMouseListener(sizeListener);

		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				startPoint = null;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				startPoint = e.getPoint();
			}
		});
		imageLabel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				endPoint = e.getPoint();
				writeDraw();
				imageLabel.repaint();
			}
		});
	}

	void setImage(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		canvasImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = canvasImage.createGraphics();
		g.drawImage(bi, 0, 0, wDrawPanel);
		//readLabel과 imageLabel 비교
		if (imageLabel != null) {
			imageLabel.setIcon(new ImageIcon(canvasImage));
			imageLabel.repaint();
		}
	}

	public void writeDraw() {
		Graphics2D g = canvasImage.createGraphics();

		if (startPoint != null) {
			g.setColor(Color.BLACK);
			g.setColor(c);
			g.setStroke(new BasicStroke(strokeSize));
			g.draw(new Line2D.Float(startPoint, endPoint));
			startPoint = endPoint;
		}
	}

	private void saveMemo() { // 메모 저장하기
		dialog = new FileDialog(this, "저장", FileDialog.SAVE);

		dialog.setVisible(true); // 다이어로그를 화면에 보여주기

		directory = dialog.getDirectory(); // 경로 가져오기
		fileName = dialog.getFile(); // 파일명 가져오기

		if (directory == null || fileName == null) {
			return;
		}

		if (check == 0) {
			writeSave();
		} else { // 그림 저장
			drawSave();
		}
	}

	private void drawSave() {
		try {
			ImageIO.write(canvasImage, "png", new File(directory + fileName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeSave() {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(directory + fileName + ".txt", true));

			String text = "제목 : " + titleField.getText() + "\n" + contentArea.getText().trim();
			bw.write(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		titleField.setText(null);
		contentArea.setText(null);
	}

	private void readMemo() { // 열기의 메서드
		open = 1;
		dialog = new FileDialog(this, "열기", FileDialog.LOAD);
		dialog.setVisible(true);

		directory = dialog.getDirectory();
		fileName = dialog.getFile();

		if (directory == null || fileName == null) {
			return;
		}

		if (fileName.substring(fileName.indexOf("."), fileName.length()).equals(".txt")) {
			writeRead();
		} else { // 그림 열기
			drawRead();
		}

	}

	private void writeRead() {
		open = 2;
		// BufferedReader사용
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(directory + fileName);
			br = new BufferedReader(fr);
			while (br.ready()) {
				readArea.append((char) br.read() + "");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void drawRead() {
		System.out.println("드로우리드");
		BufferedImage bi;
		try {
			bi = ImageIO.read(new File(directory + fileName));
			setImage(bi);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new MemoView();
	}
}
