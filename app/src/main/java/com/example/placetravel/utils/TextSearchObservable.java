package com.example.placetravel.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class TextSearchObservable implements ObservableOnSubscribe<String> {

    private Listener listener;
    private final EditText editText;

    public TextSearchObservable(EditText editText) {
        this.editText = editText;
    }

    public Listener getListener() {
        return listener;
    }

    @Override
    public void subscribe(ObservableEmitter<String> emitter) {
        listener = new Listener(emitter);
        editText.addTextChangedListener(listener);
    }

    class Listener implements TextWatcher {
        private final ObservableEmitter<String> observer;

        Listener(ObservableEmitter<String> observer) {
            this.observer = observer;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            observer.onNext(String.valueOf(charSequence));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
