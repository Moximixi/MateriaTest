package com.example.administrator.materiatest;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BookDetailActivity extends AppCompatActivity {
    public static String Intent_Book_ToEdit = "BOOKTOEDIT";
    private static final String TAG = "BookDetailActivity";
    private Book book;

    private TextView infoTitleTextView;
    private ImageView coverImageView;
    private TextView addtimeTextView;
    private RelativeLayout authorRelativeLayout;
    private TextView authorTextView;
    private RelativeLayout translatorRelativeLayout;
    private TextView translatorTextView;
    private RelativeLayout publisherRelativeLayout;
    private TextView publisherTextView;
    private RelativeLayout pubtimeRelativeLayout;
    private TextView pubtimeTextView;
    private RelativeLayout isbnRelativeLayout;
    private TextView isbnTextView;
    private RelativeLayout readingStatusRelativeLayout;
    private TextView readingStatusTextView;
    private RelativeLayout bookshelfRelativeLayout;
    private TextView bookshelfTextView;
    private RelativeLayout notesRelativeLayout;
    private TextView notesTextView;
    private RelativeLayout labelsRelativeLayout;
    private TextView labelsTextView;
    private RelativeLayout websiteRelativeLayout;
    private TextView websiteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        book=(Book)intent.getSerializableExtra(Intent_Book_ToEdit);
        setTitle(book.getTitle());

        setContentView(R.layout.activity_book_detail_content);

    }

    private void setHeader() {
        coverImageView = (ImageView) findViewById(R.id.book_detail_header_cover_image_view);
        if (book.isHasCover()) {
            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + book.getCoverPhotoFileName();
            Bitmap src = BitmapFactory.decodeFile(path);
            coverImageView.setImageBitmap(src);
            coverImageView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        }
        addtimeTextView = (TextView) findViewById(R.id.book_detail_header_addtime_text_view);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEE z");
        String addTimeString = format.format(book.getAddTime().getTime());
        addtimeTextView.setText(addTimeString);

    }

    private void setBookInfo() {
        authorRelativeLayout = (RelativeLayout) findViewById(R.id.book_info_author_item);
        authorTextView = (TextView) findViewById(R.id.book_info_author_text_view);
        translatorRelativeLayout = (RelativeLayout) findViewById(R.id.book_info_translator_item);
        translatorTextView = (TextView) findViewById(R.id.book_info_translator_text_view);
        publisherRelativeLayout = (RelativeLayout) findViewById(R.id.book_info_publisher_item);
        publisherTextView = (TextView) findViewById(R.id.book_info_publisher_text_view);
        pubtimeRelativeLayout = (RelativeLayout) findViewById(R.id.book_info_pubtime_item);
        pubtimeTextView = (TextView) findViewById(R.id.book_info_pubtime_text_view);
        isbnRelativeLayout = (RelativeLayout) findViewById(R.id.book_info_isbn_item);
        isbnTextView = (TextView) findViewById(R.id.book_info_isbn_text_view);
        infoTitleTextView = (TextView) findViewById(R.id.book_info_title_bar_text_view);

        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String authors = book.getFormatAuthor();
        if (authors!=null) {
            authorTextView.setText(authors);
            authorRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.book_info_author_toast),
                            Toast.LENGTH_SHORT)
                            .show();
                    ClipData clipData = ClipData.newPlainText(
                            getString(R.string.app_name),
                            String.format(getString(R.string.book_info_author_clipboard_content),
                                    authorTextView.getText().toString()));
                    clipboardManager.setPrimaryClip(clipData);
                    return true;
                }
            });
        } else {
            authorRelativeLayout.setVisibility(View.GONE);
        }

        String translators = book.getFormatTranslator();

        if (translators!=null) {
            translatorTextView.setText(translators);
            translatorRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.book_info_translator_toast),
                            Toast.LENGTH_SHORT)
                            .show();
                    ClipData clipData = ClipData.newPlainText(
                            getString(R.string.app_name),
                            String.format(getString(R.string.book_info_translator_clipboard_content),
                                    translatorTextView.getText().toString()));
                    clipboardManager.setPrimaryClip(clipData);
                    return true;
                }
            });
        } else {
            translatorRelativeLayout.setVisibility(View.GONE);
        }

        if (book.getPublisher().length() != 0) {
            publisherTextView.setText(book.getPublisher());
            publisherRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.book_info_publisher_toast),
                            Toast.LENGTH_SHORT)
                            .show();
                    ClipData clipData = ClipData.newPlainText(
                            getString(R.string.app_name),
                            String.format(getString(R.string.book_info_publisher_clipboard_content),
                                    publisherTextView.getText().toString()));
                    clipboardManager.setPrimaryClip(clipData);
                    return true;
                }
            });
        } else {
            publisherRelativeLayout.setVisibility(View.GONE);
        }

        Calendar calendar = book.getPubTime();
        int year = calendar.get(Calendar.YEAR);
        if (year == 9999) {
            pubtimeRelativeLayout.setVisibility(View.GONE);
        } else {
            int month = calendar.get(Calendar.MONTH) + 1;
            StringBuilder pubtime = new StringBuilder();
            pubtime.append(year);
            pubtime.append(" - ");
            pubtime.append(month);
            pubtimeTextView.setText(pubtime);
            pubtimeRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.book_info_pubtime_toast),
                            Toast.LENGTH_SHORT)
                            .show();
                    ClipData clipData = ClipData.newPlainText(
                            getString(R.string.app_name),
                            String.format(getString(R.string.book_info_pubtime_clipboard_content),
                                    pubtimeTextView.getText().toString()));
                    clipboardManager.setPrimaryClip(clipData);
                    return true;
                }
            });
        }
        if (book.getIsbn().length() != 0) {
            isbnTextView.setText(book.getIsbn());
            isbnRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.book_info_isbn_toast),
                            Toast.LENGTH_SHORT)
                            .show();
                    ClipData clipData = ClipData.newPlainText(
                            getString(R.string.app_name),
                            String.format(getString(R.string.book_info_isbn_clipboard_content),
                                    isbnTextView.getText().toString()));
                    clipboardManager.setPrimaryClip(clipData);
                    return true;
                }
            });
        } else {
            isbnRelativeLayout.setVisibility(View.GONE);
        }

        String detailBarText = String.format(getString(R.string.book_info_title),book.getDataSource());
        infoTitleTextView.setText(detailBarText);

    }

    private void setBookDetails() {
        readingStatusRelativeLayout = (RelativeLayout) findViewById(R.id.book_detail_reading_status_item);
        readingStatusTextView = (TextView) findViewById(R.id.book_detail_reading_status_text_view);
        bookshelfRelativeLayout = (RelativeLayout) findViewById(R.id.book_detail_bookshelf_item);
        bookshelfTextView = (TextView) findViewById(R.id.book_detail_bookshelf_text_view);
        notesRelativeLayout = (RelativeLayout) findViewById(R.id.book_detail_notes_item);
        notesTextView = (TextView) findViewById(R.id.book_detail_notes_text_view);
        labelsRelativeLayout = (RelativeLayout) findViewById(R.id.book_detail_labels_item);
        labelsTextView = (TextView) findViewById(R.id.book_detail_labels_text_view);
        websiteRelativeLayout = (RelativeLayout) findViewById(R.id.book_detail_website_item);
        websiteTextView = (TextView) findViewById(R.id.book_detail_website_text_view);

        readingStatusRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        BookDetailActivity.this,
                        getResources().getString(R.string.reading_status_image_view),
                        Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });
        String[] readingStatus = getResources().getStringArray(R.array.reading_status_array);
        readingStatusTextView.setText(readingStatus[book.getReadingStatus()]);

        bookshelfRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(
                        BookDetailActivity.this,
                        getResources().getString(R.string.book_shelf_image_view),
                        Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });

        BookShelf bookShelf = BookShelfLab.get(this).getBookShelf(book.getBookshelfID());
        bookshelfTextView.setText(bookShelf.getTitle());

        if (book.getNotes().length() != 0) {
            notesTextView.setText(book.getNotes());
            notesRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.note_edit_text_hint),
                            Toast.LENGTH_SHORT)
                            .show();
                    return true;
                }
            });
        } else {
            notesRelativeLayout.setVisibility(View.GONE);
        }

        List<Integer> labelID = book.getLabelID();
        if (labelID.size() != 0) {
            StringBuilder labelsTitle = new StringBuilder();
            for (int id : labelID) {
                //labelsTitle.append(LabelLab.get(this).getLabel(id).getTitle());
                labelsTitle.append(",");
            }
            labelsTitle.deleteCharAt(labelsTitle.length() - 1);
            labelsTextView.setText(labelsTitle);
            labelsRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.book_detail_labels_image_view),
                            Toast.LENGTH_SHORT)
                            .show();
                    return true;
                }
            });

        } else {
            labelsRelativeLayout.setVisibility(View.GONE);
        }

        if (book.getWebsite().length() != 0) {
            websiteTextView.setText(book.getWebsite());
            websiteRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(
                            BookDetailActivity.this,
                            getResources().getString(R.string.website_edit_text_hint),
                            Toast.LENGTH_SHORT)
                            .show();
                    return true;
                }
            });
            websiteRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(book.getWebsite()));
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    } else {
                        Log.e(TAG, "No supported browser found!");
                        Toast.makeText(BookDetailActivity.this, R.string.book_detail_browser_not_found, Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        } else {
            websiteRelativeLayout.setVisibility(View.GONE);
        }
    }


}
