package com.my.universalcopy; // আপনার প্যাকেজের নাম দিন

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

public class UniversalCopyService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        
        // ১. সার্ভিস কনফিগারেশন
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | 
                     AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        
        this.setServiceInfo(info);
        
        // ইউজারকে জানানো যে সার্ভিস চালু হয়েছে
        Toast.makeText(this, "ইউনিভার্সাল কপি চালু হয়েছে! 🚀", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // টাচ বা লং প্রেস ইভেন্ট ট্র্যাক করা
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED || 
            event.getEventType() == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED) {
            
            AccessibilityNodeInfo source = event.getSource();
            if (source == null) return;

            // টাচ করা জায়গার লেখা বের করা এবং কপি করা
            if (source.getText() != null && source.getText().length() > 0) {
                copyToClipboard(source.getText().toString());
            } else {
                // চাইল্ড ভিউ খুঁজে টেক্সট বের করার চেষ্টা
                findChildText(source);
            }
        }
    }

    // লুকানো টেক্সট খুঁজে বের করার ফাংশন
    private void findChildText(AccessibilityNodeInfo node) {
        if (node == null) return;
        
        if (node.getText() != null && node.getText().length() > 0) {
            copyToClipboard(node.getText().toString());
            return; 
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            findChildText(node.getChild(i));
        }
    }

    // ক্লিপবোর্ডে কপি করার ফাংশন
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        
        // ছোট টোস্ট মেসেজ দিয়ে ইউজারকে ফিডব্যাক দেওয়া
        Toast.makeText(getApplicationContext(), "কপি হয়েছে: " + text.substring(0, Math.min(text.length(), 20)) + "...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterrupt() {
        // সার্ভিস বন্ধ হলে
        Toast.makeText(this, "ইউনিভার্সাল কপি বন্ধ হলো।", Toast.LENGTH_SHORT).show();
    }
}
