package com.example.bmtcapitester;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.Map;

public class ApiTabFragment extends Fragment {
    private static final String ARG_TAB_DATA = "tab_data";

    private ApiTabData tabData;
    private TextView apiEndpoint;
    private LinearLayout parametersLayout;
    private TextView responseText;
    private MaterialButton testButton, clearButton, copyButton;
    private Map<String, EditText> parameterInputs = new HashMap<>();
    private ApiService apiService;
    private NestedScrollView responseScrollView;

    public static ApiTabFragment newInstance(ApiTabData tabData) {
        ApiTabFragment fragment = new ApiTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAB_DATA, tabData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabData = (ApiTabData) getArguments().getSerializable(ARG_TAB_DATA);
        }
        apiService = new ApiService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_api_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupParameterInputs();
        setupButtons();
    }

    private void initViews(View view) {
        apiEndpoint = view.findViewById(R.id.api_endpoint);
        parametersLayout = view.findViewById(R.id.parameters_layout);
        responseText = view.findViewById(R.id.response_text);
        testButton = view.findViewById(R.id.test_button);
        clearButton = view.findViewById(R.id.clear_button);
        copyButton = view.findViewById(R.id.copy_button);
        responseScrollView = view.findViewById(R.id.response_scroll_view);

        apiEndpoint.setText(tabData.getEndpoint());
    }

    private void setupParameterInputs() {
        for (ApiParameter param : tabData.getParameters()) {
            TextInputLayout textInputLayout = new TextInputLayout(requireContext());
            textInputLayout.setHint(param.getName());
            textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

            TextInputEditText editText = new TextInputEditText(textInputLayout.getContext());
            editText.setText(param.getDefaultValue());

            textInputLayout.addView(editText);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 16, 0, 0);
            textInputLayout.setLayoutParams(layoutParams);

            parametersLayout.addView(textInputLayout);
            parameterInputs.put(param.getName(), editText);
        }
    }

    private void setupButtons() {
        testButton.setOnClickListener(v -> testApi());
        clearButton.setOnClickListener(v -> clearResponse());
        copyButton.setOnClickListener(v -> copyResponse());
        // REMOVED: scroll control button listeners
    }

    private void testApi() {
        updateStatus("Testing API...");
        updateResponseStatus("TESTING", R.color.status_warning);

        Map<String, Object> payload = new HashMap<>();
        for (Map.Entry<String, EditText> entry : parameterInputs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getText().toString().trim();

            if (!value.isEmpty()) {
                if (key.endsWith("Id") || key.equals("vehicleId") || key.equals("routeid")) {
                    try {
                        payload.put(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        payload.put(key, value);
                    }
                } else if (key.equals("filterBy") || key.equals("servicetypeid") || key.equals("p_isshortesttime")) {
                    try {
                        payload.put(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        payload.put(key, 0);
                    }
                } else {
                    payload.put(key, value);
                }
            }
        }

        if (tabData.getEndpoint().contains("TripPlanner")) {
            payload.put("fromDateTime", null);
            payload.put("lan", "en");
        }

        apiService.makeApiCall(tabData.getEndpoint(), payload, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                requireActivity().runOnUiThread(() -> {
                    String formattedResponse = "=== API Test Results ===\n" +
                            "URL: " + tabData.getEndpoint() + "\n" +
                            "Status: ✅ SUCCESS\n\n" +
                            "Response Data:\n" + response;
                    responseText.setText(formattedResponse);
                    updateStatus("API test completed successfully");
                    updateResponseStatus("SUCCESS", R.color.status_success);
                    // REMOVED: auto-scroll to bottom
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    String errorResponse = "=== API Test Results ===\n" +
                            "URL: " + tabData.getEndpoint() + "\n" +
                            "Status: ❌ FAILED\n\n" +
                            "Error: " + error;
                    responseText.setText(errorResponse);
                    updateStatus("API test failed");
                    updateResponseStatus("ERROR", R.color.status_error);
                });
            }
        });
    }

    private void updateResponseStatus(String status, int colorRes) {
        TextView responseStatus = requireView().findViewById(R.id.response_status);
        if (responseStatus != null) {
            responseStatus.setText(status);
            responseStatus.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), colorRes)));
        }
    }

    private void clearResponse() {
        responseText.setText("");
        updateStatus("Response cleared");
        updateResponseStatus("Ready", R.color.status_success);
    }

    private void copyResponse() {
        String content = responseText.getText().toString();
        if (!content.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("API Response", content);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Response copied to clipboard", Toast.LENGTH_SHORT).show();
            updateStatus("Response copied to clipboard");
        }
    }

    private void updateStatus(String message) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateStatus(message);
        }
    }

    // REMOVED: scrollToTop() and scrollToBottom() methods
}
