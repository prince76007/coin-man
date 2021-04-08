package com.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bg,coin,bomb,dizzy;
	Texture[] man;
	int position=0,pause=0;
	float gravity=0.2f,velocity=0,coinY=0,bombY=0;
	Random random = new Random();
	int manY=0,level=1;
	ArrayList<Integer> coinXs= new ArrayList<>();
	ArrayList<Integer> coinYs= new ArrayList<>();
	ArrayList<Integer> bombXs=new ArrayList<>();
	ArrayList<Integer> bombYs=new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle= new ArrayList<>();
	Rectangle manRectangle ;
	int coinCount=0,bombCount=0,gameState=1,score=0,bombSpeed=7,bombLap=300;
	BitmapFont scoreFont;
	@Override
	public void create () {
		batch = new SpriteBatch();
		bg=new Texture("bg.png");
		man= new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		dizzy=new Texture("dizzy-1.png");
		manY=Gdx.graphics.getHeight()/2-man[position].getHeight()/2;
		coin = new Texture("coin.png");
		bomb= new Texture("bomb.png");
		scoreFont=new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(5);
	}

	private void makeBomb(){
		bombY=random.nextFloat();
		bombYs.add((int)(Gdx.graphics.getHeight()*bombY));
		bombXs.add(Gdx.graphics.getWidth());
	}

	private void makeCoin(){
		coinY=random.nextFloat();
		coinYs.add((int)(Gdx.graphics.getHeight()*coinY));
		coinXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		//GameBegin
		batch.begin();
		batch.draw(bg,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


		if (gameState==0){
			//game continue
			if (Gdx.input.justTouched()){
				//jump
				velocity=-10;
			}
			//createCoin
			if (coinCount<100){
				coinCount++;
			}else {
				coinCount=0;
				makeCoin();
			}
			//CoinPhysic
			coinRectangle.clear();
			for (int i=0;i<coinXs.size();i++){
				coinXs.set(i,coinXs.get(i)-4);
				batch.draw(coin,coinXs.get(i),coinYs.get(i),100,100);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),100,100));
			}
			//createBomb
			if (bombCount<bombLap){
				bombCount++;
			}else{
				bombCount=0;
				makeBomb();
			}
			//bombPhysic
			bombRectangle.clear();
			for (int j=0;j<bombXs.size();j++){
				bombXs.set(j,bombXs.get(j)-bombSpeed);
				batch.draw(bomb,bombXs.get(j),bombYs.get(j),100,100);
				bombRectangle.add(new Rectangle(bombXs.get(j),bombYs.get(j),100,100));
			}
			//manVelocity
			velocity+=gravity;
			manY-=velocity;
			//manYShouldBeScreenHeight
			if (manY<=0) {
				manY = 0;
			}else if (manY+(man[position].getHeight()/2)>=Gdx.graphics.getHeight()){
				manY=Gdx.graphics.getHeight()-(man[position].getHeight()/2);
			}


		}else if (gameState==1){
			//waiting to start
			if (Gdx.input.justTouched()){
				gameState=0;
			}
		}else if (gameState==2){
			//game over
			if (Gdx.input.justTouched()){
				gameState=0;
				score=0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				bombYs.clear();
				bombXs.clear();
				bombRectangle.clear();
				coinY=0;
				bombY=0;
				manY=Gdx.graphics.getHeight()/2-man[position].getHeight()/2;
				coinCount=0;
				bombCount=0;
				bombLap=300;
				bombSpeed=7;
			}
		}


		if (pause<6)
			pause++;
		else{
			pause=0;
			if (position<3)
				position++;
			else
				position=0;
		}




		for (int i=0;i<bombRectangle.size();i++){
			if (Intersector.overlaps(manRectangle,bombRectangle.get(i))){
				gameState=2;
			}
		}
		for (int i=0;i<coinRectangle.size();i++){
			if (Intersector.overlaps(manRectangle,coinRectangle.get(i))){
				 coinRectangle.remove(i);
				 coinXs.remove(i);
				 coinYs.remove(i);
				 score++;
			}
		}
		if (gameState==2){
			batch.draw(dizzy,Gdx.graphics.getWidth()/2-man[position].getWidth()/2,manY,150,252 );
		}else{
			batch.draw(man[position],Gdx.graphics.getWidth()/2-man[position].getWidth()/2,manY,150,252);
		}

		manRectangle=new Rectangle(Gdx.graphics.getWidth()/2-man[position].getWidth()/2,manY,150,252);
		scoreFont.draw(batch,String.valueOf(score),60,Gdx.graphics.getHeight()-150);

		if (score%9==0 && score/9>=level){
			level++;
			Gdx.app.log("levelUp","bombSpeed="+bombSpeed+" bombLap="+bombLap);
			bombSpeed+=1;
			bombLap-=10;
		}
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
