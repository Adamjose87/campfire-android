package com.pandulapeter.campfire.feature.main.home.onboarding.userData

import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.databinding.FragmentOnboardingUserDataBinding
import com.pandulapeter.campfire.feature.main.home.onboarding.OnboardingPageFragment

class UserDataFragment : OnboardingPageFragment<FragmentOnboardingUserDataBinding, UserDataViewModel>(R.layout.fragment_onboarding_user_data) {

    override val viewModel = UserDataViewModel()
}