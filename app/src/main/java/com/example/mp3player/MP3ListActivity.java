package com.example.mp3player;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class MP3ListActivity extends Activity implements OnClickListener{
    private static final String LOG = "MP3ListActivity";
    private ListView lv;
    private SeekBar seekBar;
    private Button modelButton;
    private Button backButton;
    private Button pauseButton;
    private Button nextButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
//    private Button nameButton;

    private TextView nameText;
    private ArrayList<HashMap<String, String>> musicList;
    private ArrayList<String> musicpathlist;
    private String path = Environment.getExternalStorageDirectory().getPath();
    private String musicpath;

    private String musicname;
    private MediaPlayer mediaPlayer;
    public int songNum; // 当前播放的歌曲在List中的下标
    private boolean isStartTrackingTouch;
    private Handler handler = new Handler();
    private int Sequence = 1;//顺序播放
    private int Shuffle = 2;//播放
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp3_list);

        lv = (ListView) findViewById(R.id.lv);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        modelButton = (Button) findViewById(R.id.model);
        backButton = (Button) findViewById(R.id.back);
        pauseButton = (Button) findViewById(R.id.pause);
        nextButton = (Button) findViewById(R.id.next);

//        nameButton = (Button)findViewById(R.id.nameButton);

        nameText = (TextView) findViewById(R.id.name);
        musicList = new ArrayList<HashMap<String, String>>();
        musicpathlist=new ArrayList<String>();
        mediaPlayer = new MediaPlayer();
        //获得音乐列表
        mp3List();
        pauseButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        modelButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        //进度条监听器
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());

//        nameButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MP3ListActivity.this,MusicActivity.class);
//                startActivity(intent);
//            }
//        });


        nextButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                if(arg1.getAction() == MotionEvent.ACTION_DOWN){
                    arg0.setBackgroundResource(R.drawable.xiayishou_anxia);
                }
                else if(arg1.getAction() == MotionEvent.ACTION_UP){
                    arg0.setBackgroundResource(R.drawable.xiayishou);
                }
                return false;
            }
        });

        backButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                if(arg1.getAction() == MotionEvent.ACTION_DOWN){
                    arg0.setBackgroundResource(R.drawable.shangyishou_anxia);
                }
                else if(arg1.getAction() == MotionEvent.ACTION_UP){
                    arg0.setBackgroundResource(R.drawable.shangyishou);
                }
                return false;
            }
        });
        pauseButton.setOnTouchListener(new OnTouchListener() {

       @Override
       public boolean onTouch(View arg0, MotionEvent arg1) {
          // TODO Auto-generated method stub
          int i = 0;
          i++;
          if(i % 2 == 0){

          }
          return false;
       }
    });
    }
    //获得音乐列表
    private void mp3List(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
//            String path = Environment.getExternalStorageDirectory().getPath();
            System.out.println("path-------》" + path);
            //File[] files = path.listFiles();// 读取
            String musicPath = path + "/";
            System.out.println("musicPath------>" + musicPath);
            getFileName(musicPath);  //音乐列表
            Log.i(LOG, musicPath);
        }
        /*
        *
        * */
        SimpleAdapter adapter = new SimpleAdapter(this, musicList, R.layout.sd_list, new String[] { "name" }, new int[] { R.id.mp4 });
        lv.setAdapter(adapter);
        for (int i = 0; i < musicList.size(); i++) {
            Log.i(LOG, "list.  name:  " + musicList.get(i));
        }


        /*
        * 接口回调
        * setOnItemClickListener()传入OnItemClickListener类型的参数listener
        * 其中OnItemClickListener是AdapterView类定义的内部接口，在此处调用该接口在程序运行的时候执行
        * */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
                songNum = position;
                initMediaPlayer(songNum);
            }
        });
    }
    //仅搜索当前目录下的文件
    private void getFileName(String url) {
        File files = new File(url);
        File[] file = files.listFiles();
        //先判断目录是否为空，否则会报空指针
        if (files != null) {
            for (File f : file) {
                System.out.println("file path----->" + f);
                String fileName = f.getName();
                System.out.println("file name----->" + f.getName());
                if (fileName.endsWith(".mp3")||fileName.endsWith(".wav")) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    String s = fileName.substring(0,fileName.lastIndexOf(".")).toString();
                    //获取文件的地址
                    musicpath = f.getPath();
                    System.out.println("music path ::::::::::::::::::  " + musicpath);
                    Log.i(LOG, "文件名mp3或wav：：   " + s);

                    map.put("name", fileName);
                    // map.put("mp3", f.getName());
                    System.out.println("1111111---" + fileName);
                    musicpathlist.add(musicpath);
                    musicList.add(map);
                }
            }
        }
    }
    //进度条监控
    private final class MySeekBarListener implements OnSeekBarChangeListener {
        //移动触发
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        //起始触发
        public void onStartTrackingTouch(SeekBar seekBar) {
            isStartTrackingTouch = true;
        }

        //结束触发
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
            isStartTrackingTouch = false;
        }
    }
    private void initMediaPlayer(int songNum){
        musicname = musicpathlist.get(songNum);
        Log.i(LOG, musicname);
        if (musicname != null) {
            try {
                mediaPlayer.reset(); //重置多媒体
                //指定音频文件地址
                mediaPlayer.setDataSource(musicname);
                //这是一个地址
                String path = musicpathlist.get(songNum);
                String Text[] = path.split("/");
                Log.i(LOG, Text[4]);
                //设置当前播放文件
                nameText.setText(Text[Text.length - 1]);

                Log.i(LOG, "播放");
                //准备播放
                mediaPlayer.prepare();
                start();
//             if (!mediaPlayer.isPlaying()) {
//                  mediaPlayer.start();
//                  System.out.println("开始播放");
//             }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.pause:
                pauseButton.setBackgroundResource(R.drawable.zanting);
                Toast.makeText(getApplicationContext(), "暂停", Toast.LENGTH_SHORT).show();
                pause();
                break;
            case R.id.next:
                next();
                break;
            case R.id.back:
                back();
                break;
            case R.id.model:
                int i = 0;
                i++;
                if(i % 2 == 1){
                    //随机播放
                    songNum = (int)(Math.random()*musicList.size());//随机播放
                    System.out.println("song---" + songNum);
                    Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();
                    initMediaPlayer(songNum);
                }
                else{
                    Toast.makeText(getApplicationContext(), "循环播放", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    public void start() {
        try {
            mediaPlayer.start();//开始播放
            //设置进度条长度
            seekBar.setMax(mediaPlayer.getDuration());
            //发送一个Runnable, handler收到之后就会执行run()方法
            handler.post(new Runnable() {
                public void run() {
                    // 更新进度条状态
                    if (!isStartTrackingTouch)
                        //获取当前播放音乐的位置
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    // 1秒之后再次发送
                    handler.postDelayed(this, 1000);
                }
            });
            //setOnCompletionListener 当当前多媒体对象播放完成时发生的事件
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    next();//如果当前歌曲播放完毕,自动播放下一首.
                }
            });
        } catch (Exception e) {
            Log.v("MusicService", e.getMessage());
        }
    }

    public void next() {
        Toast.makeText(getApplicationContext(), "下一首", Toast.LENGTH_SHORT).show();
        songNum = songNum == musicList.size() - 1 ? 0 : songNum + 1;
        initMediaPlayer(songNum);
    }

    public void back() {
        Toast.makeText(getApplicationContext(), "上一首", Toast.LENGTH_SHORT).show();
        //songNum = songNum == 0 ? musicList.size() - 1 : songNum - 1;
        songNum = songNum - 1 < 0 ? musicList.size() - 1 : songNum - 1;
        initMediaPlayer(songNum);
    }

    public void pause() {
        if (mediaPlayer.isPlaying())  {
            pauseButton.setBackgroundResource(R.drawable.jixu);
            mediaPlayer.pause();
        }
        else
            mediaPlayer.start();
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
