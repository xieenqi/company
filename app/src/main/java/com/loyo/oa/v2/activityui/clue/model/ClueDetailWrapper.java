package com.loyo.oa.v2.activityui.clue.model;

import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.SaleActivity;

import java.io.Serializable;

/**
 * Created by EthanGong on 16/8/23.
 */

public class ClueDetailWrapper extends BaseBean {
    public ClueDetail data;

    public class ClueDetail implements Serializable {
        public ClueSales sales;
        public SaleActivity activity;
    }
}

/**
 * "activity": {
 * "id": "57bc0433526f150730000014",
 * "creatorName": "后",
 * "createAt": 1471939635,
 * "content": "Trew8d98uery87tg3479y83oq4t4y gpohve9ps gr493qcyo8gy[w0yF 92YF9PTY\t2n yr8t3gf92\tyr0[18yf\tf9ph y7 yh[g 8fg qt8yfq wgogw9ry3297ry32yr02ofhoseh0efh293hfr932yhry932hr928yr92pu p9cu04vn0u9byvnt 98t 890eyhv 98yb m9tyb 9yhmw0vcu028[yr92yHRF9YHR0[QUr[u3[0r9u320ur3209ufr032ur0392ur0[uFCPEJQWPFJEPJFWIOFJE0U208U32U3298YR923YR298YR329Y329Y329R8Y3298YR3298Y32R9832Y9832Y932Y9RY3298R2398R3Y9832YR938Y3298Y3298",
 * "remindAt": 0,
 * "typeName": "短信",
 * "contactName": "张七仙"
 * }
 */

