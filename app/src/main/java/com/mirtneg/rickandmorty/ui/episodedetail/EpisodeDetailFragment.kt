package com.mirtneg.rickandmorty.ui.episodedetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirtneg.rickandmorty.R
import com.mirtneg.rickandmorty.databinding.FragmentEpisodeDetailBinding
import com.mirtneg.rickandmorty.ui.home.CharactersAdapter

class EpisodeDetailFragment : Fragment() {
    private lateinit var binding: FragmentEpisodeDetailBinding
    lateinit var viewModel: EpisodeDetailViewModel
    lateinit var adapter: EpisodeCharactersAdapter
    val args by navArgs<EpisodeDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEpisodeDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[EpisodeDetailViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getEpisodeById(args.episodeId)
        binding.castList.layoutManager = LinearLayoutManager(requireActivity())
        adapter = EpisodeCharactersAdapter(this::characterClick)
        binding.castList.adapter = adapter
        viewModel.episodeResponse.observe(viewLifecycleOwner){
            with(it){
                binding.episodeInfo.tagTextView.text = "Episode"
                binding.episodeInfo.dataTextView.text = episode
                binding.airDate.tagTextView.text = "Air Date"
                binding.airDate.dataTextView.text = airDate
            }
        }

        viewModel.characterData.observe(viewLifecycleOwner){
            adapter.characterItem = it
        }

        binding.backButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("character_id",adapter.itemClick.toString())
            findNavController().navigate(R.id.action_episodeDetailFragment_to_characterDetailFragment, bundle)
        }
    }

    fun characterClick(characterId: String){
        val bundle = Bundle()
        bundle.putString("character_id", characterId)
        findNavController().navigate(R.id.action_episodeDetailFragment_to_characterDetailFragment, bundle)
    }
}