package com.mirtneg.rickandmorty.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mirtneg.rickandmorty.R
import com.mirtneg.rickandmorty.data.models.Character
import com.mirtneg.rickandmorty.data.models.Episode
import com.mirtneg.rickandmorty.databinding.DialogAdvancedFiltersBinding
import com.mirtneg.rickandmorty.databinding.FragmentEpisodeDetailBinding
import com.mirtneg.rickandmorty.databinding.FragmentHomeBinding
import com.mirtneg.rickandmorty.ui.characterdetail.EpisodesAdapter

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    lateinit var viewModel: HomeViewModel

    lateinit var adapter : CharactersAdapter

    lateinit var dialog: Dialog

    lateinit var dialogBinding: DialogAdvancedFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCharacters()

        setupFilterDialog()

        binding.filterButton.setOnClickListener {
            showDetailedFilterDialog()
        }


        binding.characterList.layoutManager = LinearLayoutManager(requireActivity())
        adapter = CharactersAdapter(this::itemClick)
        binding.characterList.adapter = adapter

        viewModel.charactersList.observe(viewLifecycleOwner, {
           adapter.characterItem = it
        })

        viewModel.filterList.observe(viewLifecycleOwner,{
            adapter.characterItem = it
        })

        binding.searchEditText.doOnTextChanged { text, start, before, count ->
            //val searchResult = mutableListOf<Character>()
            viewModel.charactersList.value?.let { safeCharacter ->
                adapter.characterItem = safeCharacter.filter { character ->
                    character.name.startsWith(text.toString(), true) }
            }
        }
    }

    private fun setupFilterDialog(){
        dialog = Dialog(requireActivity())
        dialogBinding = DialogAdvancedFiltersBinding.inflate(requireActivity().layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val layoutParams = dialog.window!!.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams

        dialogBinding.closeButton.setOnClickListener {

            dialog.dismiss()
        }

        dialogBinding.applyButton.setOnClickListener {
            if (viewModel.showingSearchResult){
                viewModel.showingSearchResult = false
                viewModel.charactersList.value?.let{
                    adapter.characterItem = it
                }
                dialogBinding.speciesInputEditText.setText("")
                dialogBinding.genderInputEditText.setText("")
                dialogBinding.statusInputEditText.setText("")
            }
            else{
                viewModel.filterCharacters(
                    dialogBinding.speciesInputEditText.text.toString(),
                    dialogBinding.genderInputEditText.text.toString(),
                    dialogBinding.statusInputEditText.text.toString())
            }
            dialog.dismiss()
        }
    }

    private fun showDetailedFilterDialog() {
        if (viewModel.showingSearchResult){
            dialogBinding.applyButton.setText("Clear Filters")
        }
        else{
            dialogBinding.applyButton.setText("Apply")

        }
        dialog.show()
    }

    private fun itemClick(characterId : String) {
        val bundle = Bundle()
        bundle.putString("character_id", characterId)
        findNavController().navigate(R.id.action_homeFragment_to_characterDetailFragment, bundle)
    }
}