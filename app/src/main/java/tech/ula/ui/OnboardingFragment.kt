package tech.ula.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tech.ula.R
import tech.ula.databinding.FragOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnGetStarted.setOnClickListener {
            requireActivity().getSharedPreferences("onboarding", android.content.Context.MODE_PRIVATE)
                .edit().putBoolean("completed", true).apply()
            findNavController().navigate(R.id.action_onboarding_to_app_list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
