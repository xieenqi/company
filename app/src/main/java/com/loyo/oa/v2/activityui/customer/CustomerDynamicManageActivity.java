package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.DynamicListnestingAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AudioViewModel;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.CustomerFollowUpModel;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Player;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【跟进动态】 客户管理
 */
public class CustomerDynamicManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2, MediaPlayer.OnCompletionListener {

    public static final int ACTIVITIES_ADD = 101;

    private ImageView layout_audio_close;
    private ProgressBar layout_progressbar;
    private ImageView layout_audio_pauseorplay;
    private SaleActivitiesAdapter listAdapter;
    private PullToRefreshListView lv_saleActivity;
    private TextView tv_audio_starttime, tv_audio_endtime;
    private ViewGroup layout_audioplayer, layout_audio_contral;
    private ViewGroup img_title_left, layout_add, layout_view_bottom;
    private ViewGroup layout_last,layout_next;

    private Customer customer;
    private SaleActivity mSaleActivity;

    private DynamicListnestingAdapter nestionListAdapter;
    private ArrayList<AudioViewModel> lstData_saleActivity_current = new ArrayList<>();
    private PaginationX<CustomerFollowUpModel> paginationX = new PaginationX<>(20);

    private Player player;
    private SeekBar musicProgress;

    private boolean isChanged = false;
    private boolean isTopAdd = true;
    private boolean isMyUser;
    private boolean isOnPlay;
    private String playTime;

    private AudioViewModel mViewModel;/*当前播放音频的model*/
    private int mPosition;            /*当前播放音频的下标*/


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                /*打开关闭播放器*/
                case 0x01:
                    if (layout_audioplayer.getVisibility() == View.VISIBLE) {
                        layout_audioplayer.setVisibility(View.GONE);
                        if (isMyUser)
                            layout_add.setVisibility(View.VISIBLE);
                    } else if (layout_audioplayer.getVisibility() == View.GONE) {
                        layout_audioplayer.setVisibility(View.VISIBLE);
                        layout_add.setVisibility(View.GONE);
                    }
                    break;

                /*播放暂停*/
                case 0x02:
                    if (isOnPlay) {
                        audioStart();
                    } else {
                        audioPause();
                    }
                    break;

                /*播放时间*/
                case 0x03:
                    tv_audio_starttime.setText(playTime);
                    break;

                /*播放停止*/
                case 0x04:
                    playTime = "00:00:00";
                    musicProgress.setProgress(0);
                    layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                    isOnPlay = true;
                    mViewModel.setIsAnim(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_activities_manage);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            customer = (Customer) bundle.getSerializable(Customer.class.getName());
            isMyUser = bundle.getBoolean("isMyUser");
        }
        setTitle("跟进动态");
        initUI();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (customer != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", paginationX.getPageIndex());
            map.put("pageSize", isTopAdd ? lstData_saleActivity_current.size() >= 20 ? lstData_saleActivity_current.size() : 20 : 20);
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSaleactivity(customer.getId(), map, new RCallback<PaginationX<CustomerFollowUpModel>>() {
                @Override
                public void success(final PaginationX<CustomerFollowUpModel> paginationXes, final Response response) {
                    HttpErrorCheck.checkResponse("跟进动态数据:", response);
                    lv_saleActivity.onRefreshComplete();
                    if (!PaginationX.isEmpty(paginationXes)) {
                        paginationX = paginationXes;
                        if (isTopAdd) {
                            lstData_saleActivity_current.clear();
                        }

                        List<CustomerFollowUpModel> list = paginationX.getRecords();
                        for (int i = 0; list != null && i < list.size(); i++) {
                            CustomerFollowUpModel model = list.get(i);
                            lstData_saleActivity_current.add(new AudioViewModel(model));
                        }
                        bindData();
                    }
                }

                @Override
                public void failure(final RetrofitError error) {
                    super.failure(error);
                    HttpErrorCheck.checkError(error);
                    lv_saleActivity.onRefreshComplete();
                }
            });
        }
    }

    private void initUI() {
        tv_audio_starttime = (TextView) findViewById(R.id.tv_audio_starttime);
        tv_audio_endtime = (TextView) findViewById(R.id.tv_audio_endtime);
        layout_progressbar = (ProgressBar) findViewById(R.id.layout_progressbar);
        layout_audio_close = (ImageView) findViewById(R.id.layout_audio_close);
        layout_audio_pauseorplay = (ImageView) findViewById(R.id.layout_audio_pauseorplay);
        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        layout_audioplayer = (ViewGroup) findViewById(R.id.layout_audioplayer);
        layout_audio_contral = (ViewGroup) findViewById(R.id.layout_audio_contral);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        layout_last = (ViewGroup) findViewById(R.id.layout_last);
        layout_next = (ViewGroup) findViewById(R.id.layout_next);

        lv_saleActivity = (PullToRefreshListView) findViewById(R.id.lv_saleActivity);

        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        layout_add.setOnTouchListener(Global.GetTouch());
        layout_last.setOnTouchListener(Global.GetTouch());
        layout_next.setOnTouchListener(Global.GetTouch());
        layout_audio_close.setOnTouchListener(Global.GetTouch());
        layout_add.setOnClickListener(this);
        layout_audio_close.setOnClickListener(this);
        layout_audio_pauseorplay.setOnClickListener(this);
        layout_last.setOnClickListener(this);
        layout_next.setOnClickListener(this);


        player = new Player(musicProgress);
        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        //layout_audioplayer.getBackground().setAlpha(50);
        //layout_view_bottom.getBackground().setAlpha(50);

        player.mediaPlayer.setOnCompletionListener(this);
    }

    /**
     * Player启动与停止回调
     * */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mp.getDuration() != 0){
            mHandler.sendEmptyMessage(0x04);
        }
    }

    /**
     * 拖动条监听
     * */
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
            playTime = DateTool.stringForTime(this.progress);
            mHandler.sendEmptyMessage(0x03);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            playTime = DateTool.stringForTime(progress);
            player.mediaPlayer.seekTo(progress);
            mHandler.sendEmptyMessage(0x03);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*上一首*/
            case R.id.layout_last:

                if(mPosition == 0){
                    Toast("这是第一条");
                }else{
                    mPosition--;
                    //Toast(""+mPosition);
                    if(!TextUtils.isEmpty(lstData_saleActivity_current.get(mPosition).getAudioUrl())){
                        mViewModel = lstData_saleActivity_current.get(mPosition);
                        audioPlayDeal(mPosition, lstData_saleActivity_current.get(mPosition));
                    }else{
                        Toast("没有录音。");
                    }
                }
                break;

            /*下一首*/
            case R.id.layout_next:

                if(mPosition == lstData_saleActivity_current.size() - 1){
                    Toast("这是最后一条");
                }else{
                    mPosition++;
                    //Toast("" + mPosition);
                    if(!TextUtils.isEmpty(lstData_saleActivity_current.get(mPosition).getAudioUrl())){
                        mViewModel = lstData_saleActivity_current.get(mPosition);
                        audioPlayDeal(mPosition, lstData_saleActivity_current.get(mPosition));
                    }else{
                        Toast("没有录音。");
                    }
                }
                break;

            /*播放暂停*/
            case R.id.layout_audio_pauseorplay:
                mHandler.sendEmptyMessage(0x02);
                break;

            /*关闭播放器*/
            case R.id.layout_audio_close:
                stopPlayer();
                mHandler.sendEmptyMessage(0x01);
                break;

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*新建*/
            case R.id.layout_add:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.class.getName(), customer);
                app.startActivityForResult(this, CustomerDynamicAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            /*新建跟进动态回调*/
            case ACTIVITIES_ADD:
                isChanged = true;
                getData();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        paginationX.setPageIndex(paginationX.getPageIndex() + 1);
        getData();
    }

    void bindData() {
        if (null == listAdapter) {
            listAdapter = new SaleActivitiesAdapter();
            lv_saleActivity.setAdapter(listAdapter);
            lv_saleActivity.setMode(PullToRefreshBase.Mode.BOTH);
            lv_saleActivity.setOnRefreshListener(this);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 播放开始
     * */
    void audioStart(){
        isOnPlay = false;
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
        if (player != null) {
            player.play();
        }
    }

    /**
     * 播放暂停
     * */
    void audioPause(){
        isOnPlay = true;
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
        if (player != null) {
            player.pause();
        }
    }

    /**
     * 播放停止
     * */
    void stopPlayer() {
        if (player != null) {
            player.stop();
            player = null;
        }
        if(null != mViewModel)
        mViewModel.setIsAnim(false);
    }

    /**
     * 播放和动画处理
     * */
    void audioPlayDeal(int position, final AudioViewModel viewModel){

        try {
            tv_audio_endtime.setText(DateTool.stringForTime((int)viewModel.audioLength * 1000));
        }catch (NullPointerException e){
            Toast("录音文件不存在！");
            e.printStackTrace();
            return;
        }

        for(int i = 0;i<lstData_saleActivity_current.size();i++){
            if(i != position){
                lstData_saleActivity_current.get(i).setIsAnim(false);
            }
        }

        viewModel.setIsAnim(true);

        if (layout_audioplayer.getVisibility() != View.VISIBLE) {
            mHandler.sendEmptyMessage(0x01);
        }
        if (null == player) {
            player = new Player(musicProgress);
        }
        audioStart();
        playTime = "00:00:00";

        new Thread(new Runnable() {
            @Override
            public void run() {
                player.playUrl(viewModel.audioUrl);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            Intent intent = new Intent();
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, FinalVariables.REQUEST_CREATE_TASK, intent);
            return;
        }
        super.onBackPressed();
    }

    private class SaleActivitiesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstData_saleActivity_current.size();
        }

        @Override
        public Object getItem(final int i) {
            return lstData_saleActivity_current.isEmpty() ? null : lstData_saleActivity_current.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_saleactivities_group_child, null);
            }
            final AudioViewModel viewModel = lstData_saleActivity_current.get(position);

            LinearLayout ll_layout_time = ViewHolder.get(convertView, R.id.ll_layout_time);
            LinearLayout layout_audio = ViewHolder.get(convertView, R.id.layout_audio);
            ListView lv_listview = ViewHolder.get(convertView, R.id.lv_listview);
            TextView tv_create_time = ViewHolder.get(convertView, R.id.tv_create_time);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
            TextView tv_contact_name = ViewHolder.get(convertView, R.id.tv_contact_name);
            TextView tv_follow_name = ViewHolder.get(convertView, R.id.tv_follow_name);
            TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
            TextView tv_audio_length = ViewHolder.get(convertView, R.id.tv_audio_length);
            ImageView iv_imgTime = ViewHolder.get(convertView, R.id.iv_imgTime);
            final TextView tv_calls = ViewHolder.get(convertView, R.id.iv_calls);

            if (viewModel.getIsAnim()) {
                app.startAnim(tv_calls);
            }
            else {
                app.stopAnim(tv_calls);
            }
            viewModel.imageViewWeakReference = new WeakReference<TextView>(tv_calls);

            tv_create_time.setText(DateTool.getDiffTime(viewModel.getCreateAt()));
            tv_content.setText(viewModel.getContent());

            /*判断是否有录音*/
            if (null != viewModel.audioUrl && !TextUtils.isEmpty(viewModel.audioUrl)) {

                long audioLength = viewModel.audioLength;
  /*              if(audioLength > 0 && audioLength < 60){
                    tv_calls.setText("00000");
                }else if(audioLength > 60 && audioLength < 300){
                    tv_calls.setText("0000000");
                }else if(audioLength > 300 && audioLength < 600){
                    tv_calls.setText("000000000");
                }else if(audioLength > 600 && audioLength < 1200){
                    tv_calls.setText("000000000000");
                }else if(audioLength > 1200 && audioLength < 1800){
                    tv_calls.setText("000000000000000");
                }else if(audioLength > 1800){
                    tv_calls.setText("000000000000000000");
                }else{
                    tv_calls.setText("");
                }*/

                if(audioLength > 0 && audioLength < 10){
                    tv_calls.setText("00000");
                }else if(audioLength > 10 && audioLength < 20){
                    tv_calls.setText("0000000");
                }else if(audioLength > 20 && audioLength < 30){
                    tv_calls.setText("000000000");
                }else if(audioLength > 30){
                    tv_calls.setText("00000000000");
                }else{
                    tv_calls.setText("");
                }

                layout_audio.setVisibility(View.VISIBLE);
                tv_audio_length.setText(DateTool.stringForTime((int) viewModel.audioLength * 1000));
            } else {
                layout_audio.setVisibility(View.GONE);
            }

            tv_contact_name.setText("联系人：" + viewModel.contactName);
            tv_follow_name.setText("跟进人：" + viewModel.creatorName + " #" + viewModel.typeName);

            if (null != viewModel.getAttachments() && viewModel.getAttachments().size() != 0) {
                lv_listview.setVisibility(View.VISIBLE);
                nestionListAdapter = new DynamicListnestingAdapter(viewModel.getAttachments(), mContext);
                lv_listview.setAdapter(nestionListAdapter);
            } else {
                lv_listview.setVisibility(View.GONE);
            }

            if (viewModel.getRemindAt() != 0) {
                ll_layout_time.setVisibility(View.VISIBLE);
                tv_time.setText(app.df3.format(new Date(viewModel.getRemindAt() * 1000)));
            } else {
                ll_layout_time.setVisibility(View.GONE);
            }

            /*提醒时间没有过当前时间变红色*/
            if (viewModel.getRemindAt() > System.currentTimeMillis() / 1000) {
                tv_time.setTextColor(getResources().getColor(R.color.red1));
                iv_imgTime.setImageResource(R.drawable.icon_tx2);
            } else {
                tv_time.setTextColor(getResources().getColor(R.color.text99));
                iv_imgTime.setImageResource(R.drawable.icon_tx1);
            }
            if (position == lstData_saleActivity_current.size() - 1) {
                convertView.setBackgroundResource(R.drawable.item_bg_buttom);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            }

            /*播放录音*/
            layout_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = position;
                    mViewModel = viewModel;
                    audioPlayDeal(position, viewModel);
                }
            });

            return convertView;
        }
    }
}