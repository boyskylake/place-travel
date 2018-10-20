package com.example.placetravel.place;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.database.PlaceDatabase;
import com.example.placetravel.database.UserSpf;
import com.example.placetravel.model.CommentModel;
import com.example.placetravel.utils.DateTimeUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends BaseActivity implements PlaceDatabase.CommentListener {

    private AppCompatEditText etComment;
    private AppCompatTextView tvEmpty;
    private View progressBar;
    private RecyclerView rvComment;
    private UserSpf userSpf;
    private FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> adapter;
    private DatabaseReference comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.txt_all_comment);

        userSpf = new UserSpf(this);

        if (FirebaseAuth.getInstance().getUid() == null || isOwner()) {
            findViewById(R.id.controlComment).setVisibility(View.GONE);
        }

        tvEmpty = findViewById(R.id.tvEmpty);
        etComment = findViewById(R.id.etComment);
        progressBar = findViewById(R.id.viewProgressBar);

        findViewById(R.id.btnSendComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etComment.getText().toString();
                if (!message.trim().isEmpty()) {
                    CommentModel commentModel = new CommentModel();
                    commentModel.setMessage(message);
                    commentModel.setPlaceId(getPlaceId());
                    commentModel.setImageUrl(userSpf.getImageUrl());
                    commentModel.setUserId(FirebaseAuth.getInstance().getUid());
                    commentModel.setCreateTimestamp(DateTimeUtil.getTimeUTC());
                    PlaceDatabase.comment(commentModel, CommentActivity.this);
                } else {
                    Toast.makeText(CommentActivity.this, R.string.txt_require_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvComment = findViewById(R.id.rvComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        comment = database.getReference("comment");
        Query query = comment.child(getPlaceId());

        FirebaseRecyclerOptions<CommentModel> options = new FirebaseRecyclerOptions.Builder<CommentModel>()
                .setQuery(query, CommentModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                progressBar.setVisibility(View.GONE);
                if (adapter.getItemCount() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentModel model) {
                holder.onBindData(model);
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
            }

        };
        adapter.startListening();
        rvComment.setAdapter(adapter);

        if (userSpf.isAdminLogin()){
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    AlertDialog alertDialog = new AlertDialog.Builder(CommentActivity.this)
                            .setTitle(getString(R.string.txt_delete_comment))
                            .setMessage(getString(R.string.txt_des_delete_comment))
                            .setPositiveButton(getText(R.string.txt_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    int positons = viewHolder.getAdapterPosition();
                                    Object object = adapter.getRef(positons);
                                    comment.child(getPlaceId()).child(((DatabaseReference) object).getKey()).removeValue();
                                }
                            })
                            .setNegativeButton(getText(R.string.txt_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    rvComment.setAdapter(adapter);
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }).attachToRecyclerView(rvComment);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    public void onCommentSuccess() {
        etComment.getText().clear();
        rvComment.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void onCommentFailure() {
        Toast.makeText(this, R.string.txt_please_try_again, Toast.LENGTH_SHORT).show();
    }

    private String getPlaceId() {
        return getIntent().getStringExtra("place_id");
    }

    private boolean isOwner() {
        return getIntent().getBooleanExtra("is_owner", false);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfile;
        private AppCompatTextView tvMessage;

        CommentViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }

        @SuppressLint("CheckResult")
        void onBindData(CommentModel model) {
            tvMessage.setText(model.getMessage());
            RequestOptions options = new RequestOptions();
            options.placeholder(android.R.color.darker_gray);
            options.error(R.drawable.ic_avatar);
            if (model.getImageUrl().startsWith("https://graph.facebook.com/")) {
                Glide.with(itemView.getContext())
                        .applyDefaultRequestOptions(options)
                        .load(model.getImageUrl()).into(imgProfile);
            } else {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference islandRef = storageRef.child("user_profile/" + model.getImageUrl());
                Glide.with(itemView.getContext())
                        .applyDefaultRequestOptions(options)
                        .load(islandRef).into(imgProfile);
            }
        }

    }
}
