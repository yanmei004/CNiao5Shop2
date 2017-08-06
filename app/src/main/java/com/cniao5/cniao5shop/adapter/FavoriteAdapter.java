package com.cniao5.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.Favorite;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 收藏
 */
public class FavoriteAdapter extends SimpleAdapter<Favorite> {

    private FavoriteLisneter mFavoriteLisneter;

    public FavoriteAdapter(Context context, List<Favorite> datas,FavoriteLisneter favoriteLisneter) {
        super(context, datas, R.layout.template_favorite_item);
        this.mFavoriteLisneter = favoriteLisneter;
    }

    @Override
    public void bindData(BaseViewHolder holder, final Favorite favorite) {
        Wares wares = favorite.getWares();
        holder.getTextView(R.id.tv_title).setText(wares.getName());
        holder.getTextView(R.id.tv_price).setText("￥ " + wares.getPrice());

        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        Button buttonRemove = holder.getButton(R.id.btn_remove);
        Button buttonLike = holder.getButton(R.id.btn_like);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavoriteLisneter != null)
                    mFavoriteLisneter.onClickDelete(favorite);
            }
        });

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(mContext, "功能正在完善...");
            }
        });


    }

    public interface FavoriteLisneter {

        void onClickDelete(Favorite favorite);

    }
}
