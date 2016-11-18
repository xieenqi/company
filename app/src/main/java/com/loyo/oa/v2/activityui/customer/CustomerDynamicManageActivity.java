package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.customer.adapter.DynamicListnestingAdapter;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.DynamicAddActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AudioViewModel;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.CustomerFollowUpModel;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ViewGroup layout_last, layout_next;

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
    int screenWidth;

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
                    LogUtil.dee("播放停止");
                    playTime = "00:00:00";
                    musicProgress.setProgress(0);
                    layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
                    isOnPlay = true;
                    mViewModel.setIsAnim(false);
                    break;
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;     // 屏幕宽度（像素）
        initUI();
        getData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 获取数据
     */
    private void getData() {
        showLoading("");
        if (customer != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", paginationX.getPageIndex());
            map.put("pageSize", isTopAdd ? lstData_saleActivity_current.size() >= 5 ? lstData_saleActivity_current.size() : 5 : 5);
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
        player.mediaPlayer.setOnCompletionListener(this);
    }

    /**
     * Player启动与停止回调
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.getDuration() != 0) {
            mHandler.sendEmptyMessage(0x04);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CustomerDynamicManage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * 拖动条监听
     */
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
        killPlayer();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*上一首*/
            case R.id.layout_last:

                if (mPosition == 0) {
                    sweetAlertDialogView.alertMessage("提示", "已到第一条跟进!");
                    mHandler.sendEmptyMessage(0x04);
                    audioPause();
                } else {
                    mPosition--;
                    if (!TextUtils.isEmpty(lstData_saleActivity_current.get(mPosition).getAudioUrl())) {
                        mViewModel = lstData_saleActivity_current.get(mPosition);
                        audioPlayDeal(mPosition, lstData_saleActivity_current.get(mPosition));
                    } else {
                        Toast("此跟进无电话录音。");
                        mHandler.sendEmptyMessage(0x04);
                        audioPause();
                    }
                }
                break;

            /*下一首*/
            case R.id.layout_next:

                if (mPosition == lstData_saleActivity_current.size() - 1) {
                    sweetAlertDialogView.alertMessage("提示", "已到本页最后一条跟进,\n请下拉跟进列表获取更多跟进信息!");
                    mHandler.sendEmptyMessage(0x04);
                    audioPause();
                } else {
                    mPosition++;
                    if (!TextUtils.isEmpty(lstData_saleActivity_current.get(mPosition).getAudioUrl())) {
                        mViewModel = lstData_saleActivity_current.get(mPosition);
                        audioPlayDeal(mPosition, lstData_saleActivity_current.get(mPosition));
                    } else {
                        Toast("此跟进无电话录音。");
                        mHandler.sendEmptyMessage(0x04);
                        audioPause();
                    }
                }
                break;

            /*播放暂停*/
            case R.id.layout_audio_pauseorplay:
                mHandler.sendEmptyMessage(0x02);
                break;

            /*关闭播放器*/
            case R.id.layout_audio_close:
                killPlayer();
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
                bundle.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                app.startActivityForResult(this, DynamicAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);
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
     */
    void audioStart() {
        isOnPlay = false;
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_pause);
        if (null != mViewModel)
            mViewModel.setIsAnim(true);
        if (player != null) {
            player.play();
        }
    }

    /**
     * 播放暂停
     */
    void audioPause() {
        isOnPlay = true;
        layout_audio_pauseorplay.setBackgroundResource(R.drawable.icon_audio_play);
        if (null != mViewModel)
            mViewModel.setIsAnim(false);
        if (player != null) {
            player.pause();
        }
    }

    /**
     * 杀死Player
     */
    void killPlayer() {
        if (player != null) {
            player.stop();
            player = null;
        }
        if (null != mViewModel)
            mViewModel.setIsAnim(false);
    }

    /**
     * 线程池播放Player
     */
    void threadPool(final String url) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                player.playUrl(url);
            }
        });
    }

    /**
     * 播放和动画处理
     */
    void audioPlayDeal(int position, final AudioViewModel viewModel) {

        try {
            tv_audio_endtime.setText(DateTool.stringForTime((int) viewModel.audioLength * 1000));
        } catch (NullPointerException e) {
            Toast("录音文件不存在！");
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < lstData_saleActivity_current.size(); i++) {
            if (i != position) {
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

        threadPool(viewModel.audioUrl);
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

    private String webCode = "<p style=\"text-indent:37px\"><span style=\"font-size:40px;font-family:方正仿宋简体\">2016</span><span style=\"font-size:40px;font-family:方正仿宋简体\">年6月24日</span><span style=\"font-size:40px;font-family:方正仿宋简体\">上午</span><span style=\"font-size:40px;font-family:方正仿宋简体\">,</span><span style=\"font-size:40px;font-family:方正仿宋简体\">简阳市教科局一fnsdiIQWDKAIUFDC SDDFnvofbdfv dfbdfvsd,feisfdb dfbmdifsfkngsdlv'vdfmisdjfps,fksdnfsdvxvsnfwekge.gf,vdfkvdfekjbckndfop[f[科田馨科长、</span><span style=\"font-size:40px;font-family:方正仿宋简体\">成都市教育技术装备管理中心</span><span style=\"font-size:40px;font-family:方正仿宋简体\">罗虹副主任、电教信息科倪宏科长、设备管理科夏涛副科长、周陶、金红老师到会指导，龙泉驿区教育局相关领导参加了此次活动。</span></p><p style=\"text-indent:37px\"><span style=\"font-size:40px;font-family:方正仿宋简体\">首先是龙泉七中罗登远校长带领现场参观学校的装备管理工作，随后开展了工作交流。会上，龙泉驿区政府教育督导室原主任林松权同志介绍了龙泉驿区接受四川省义务教育均衡发展县检查工作情况，</span><span style=\"font-size:40px;font-family:方正仿宋简体\">四川省教育厅技术物资装备管理指导中心实验教学科田馨科长进行了省</span><span style=\"font-size:40px;font-family:方正仿宋简体\">义务教育均衡发展县检查</span><span style=\"font-size:40px;font-family:方正仿宋简体\">专题培训，并现场答疑，最后，</span><span style=\"font-size:40px;font-family:方正仿宋简体\">成都市教育技术装备管理中心罗虹副主任讲话，并对简阳市今后的迎检准备工作提出具体要求。</span></p><p><img src=\"http://www.cdjzs.com/ueditor/net/upload/image/20160628/6360271029920046193019174.png\" title=\"QQ截图20160628112935.png\" alt=\"QQ截图20160628112935.png\"/><img src=\"http://www.cdjzs.com/ueditor/net/upload/image/20160628/6360271030937167978310009.png\" title=\"QQ截图20160628113013.png\" alt=\"QQ截图20160628113013.png\"/><img src=\"http://www.cdjzs.com/ueditor/net/upload/image/20160628/6360271031267888555254636.png\" title=\"QQ截图20160628113027.png\" alt=\"QQ截图20160628113027.png\"/><img src=\"http://www.cdjzs.com/ueditor/net/upload/image/20160628/6360271030562767318666623.png\" title=\"QQ截图20160628112957.png\" alt=\"QQ截图20160628112957.png\"/></p>";

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
            final AudioViewModel viewModel = lstData_saleActivity_current.get(position);
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.item_saleactivities_group_child, null);
                holder.ll_layout_time = ViewHolder.get(convertView, R.id.ll_layout_time);
                holder.layout_audio = ViewHolder.get(convertView, R.id.layout_audio);
                holder.lv_listview = ViewHolder.get(convertView, R.id.lv_listview);
                holder.tv_create_time = ViewHolder.get(convertView, R.id.tv_create_time);
                holder.tv_content = ViewHolder.get(convertView, R.id.tv_content);
                holder.tv_contact_name = ViewHolder.get(convertView, R.id.tv_contact_name);
                holder.tv_follow_name = ViewHolder.get(convertView, R.id.tv_follow_name);
                holder.tv_time = ViewHolder.get(convertView, R.id.tv_time);
                holder.tv_audio_length = ViewHolder.get(convertView, R.id.tv_audio_length);
                holder.iv_imgTime = ViewHolder.get(convertView, R.id.iv_imgTime);
                holder.tv_calls = ViewHolder.get(convertView, R.id.iv_calls);
                holder.ll_web = ViewHolder.get(convertView, R.id.ll_web);//装在webview的容器
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(holder.ll_web, viewModel.getContent());

            if (viewModel.getIsAnim()) {
                app.startAnim(holder.tv_calls);
            } else {
                app.stopAnim(holder.tv_calls);
            }
            viewModel.imageViewWeakReference = new WeakReference<TextView>(holder.tv_calls);

            holder.tv_create_time.setText(DateTool.getDiffTime(viewModel.getCreateAt()));
            if (!viewModel.getContent().contains("<p>")) {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(viewModel.getContent());
            } else {
                holder.tv_content.setVisibility(View.GONE);
            }
//            holder.tv_content.setText(Html.fromHtml(webCode));

            /*判断是否有录音*/
            if (null != viewModel.audioUrl && !TextUtils.isEmpty(viewModel.audioUrl)) {

                long audioLength = viewModel.audioLength;
                if (audioLength > 0 && audioLength <= 60) {
                    holder.tv_calls.setText("000");
                } else if (audioLength > 60 && audioLength <= 300) {
                    holder.tv_calls.setText("00000");
                } else if (audioLength > 300 && audioLength <= 600) {
                    holder.tv_calls.setText("0000000");
                } else if (audioLength > 600 && audioLength <= 1200) {
                    holder.tv_calls.setText("000000000");
                } else if (audioLength > 1200 && audioLength <= 1800) {
                    holder.tv_calls.setText("00000000000");
                } else if (audioLength > 1800 && audioLength <= 3600) {
                    holder.tv_calls.setText("00000000000000");
                } else if (audioLength > 3600) {
                    holder.tv_calls.setText("0000000000000000");
                } else {
                    holder.tv_calls.setText("");
                }

                holder.layout_audio.setVisibility(View.VISIBLE);
                holder.tv_audio_length.setText(DateTool.stringForTime((int) viewModel.audioLength * 1000));
            } else {
                holder.layout_audio.setVisibility(View.GONE);
            }

            holder.tv_contact_name.setText("联系人：" + viewModel.contactName);
            holder.tv_follow_name.setText("跟进人：" + viewModel.creatorName + " #" + viewModel.typeName);

            if (null != viewModel.getAttachments() && viewModel.getAttachments().size() != 0) {
                holder.lv_listview.setVisibility(View.VISIBLE);
                nestionListAdapter = new DynamicListnestingAdapter(viewModel.getAttachments(), mContext);
                holder.lv_listview.setAdapter(nestionListAdapter);
            } else {
                holder.lv_listview.setVisibility(View.GONE);
            }

            if (viewModel.getRemindAt() != 0) {
                holder.ll_layout_time.setVisibility(View.VISIBLE);
                holder.tv_time.setText(app.df3.format(new Date(viewModel.getRemindAt() * 1000)));
            } else {
                holder.ll_layout_time.setVisibility(View.GONE);
            }

            /*提醒时间没有过当前时间变红色*/
            if (viewModel.getRemindAt() > System.currentTimeMillis() / 1000) {
                holder.tv_time.setTextColor(getResources().getColor(R.color.red1));
                holder.iv_imgTime.setImageResource(R.drawable.icon_tx2);
            } else {
                holder.tv_time.setTextColor(getResources().getColor(R.color.text99));
                holder.iv_imgTime.setImageResource(R.drawable.icon_tx1);
            }
            if (position == lstData_saleActivity_current.size() - 1) {
                convertView.setBackgroundResource(R.drawable.item_bg_buttom);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            }

            /*播放录音*/
            holder.layout_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = position;
                    mViewModel = viewModel;
                    audioPlayDeal(position, viewModel);
                }
            });

            return convertView;
        }


        class Holder {
            LinearLayout ll_layout_time;
            LinearLayout layout_audio, ll_web;
            ListView lv_listview;
            TextView tv_create_time;
            TextView tv_content;
            TextView tv_contact_name;
            TextView tv_follow_name;
            TextView tv_time;
            TextView tv_audio_length;
            ImageView iv_imgTime;
            TextView tv_calls;

            /**
             * 设置图文混编
             */
            public void setContent(LinearLayout layout, String content) {
                layout.removeAllViews();
                for (final ImgAndText ele : CommonHtmlUtils.Instance().checkContentList(content)) {
                    if (ele.type.startsWith("img")) {
                        CommonImageView img = new CommonImageView(CustomerDynamicManageActivity.this, ele.data);
                        layout.addView(img);
                    } else {
                        CommonTextVew tex = new CommonTextVew(CustomerDynamicManageActivity.this, ele.data);
                        layout.addView(tex);
                    }
                }
                layout.setVisibility(View.VISIBLE);
            }
        }
    }
}