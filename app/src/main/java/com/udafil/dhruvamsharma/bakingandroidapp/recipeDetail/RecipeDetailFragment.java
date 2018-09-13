package com.udafil.dhruvamsharma.bakingandroidapp.recipeDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.udafil.dhruvamsharma.bakingandroidapp.R;
import com.udafil.dhruvamsharma.bakingandroidapp.RecipeWidget;
import com.udafil.dhruvamsharma.bakingandroidapp.data.model.RecipeModel;
import com.udafil.dhruvamsharma.bakingandroidapp.detail.DetailActivity;

import org.parceler.Parcels;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RecipeDetailFragment extends Fragment implements VerticalStepperForm{

    private OnFragmentInteractionListener mListener;
    private RecipeModel recipeData;
    private ImageView noDetails;
    private Boolean mTwoPane;
    private VerticalStepperFormLayout verticalStepperForm;
    private String[] mySteps;

    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private Button changeRecipeButton;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        setupFragment(view);

        return view;
    }

    private void setupFragment(View view) {


        Bundle bundle;

        if (getArguments() != null) {

            bundle = getArguments();

            recipeData = Parcels.unwrap(bundle.getParcelable(getContext().getPackageName()));
            if(bundle.containsKey("isTwoPane"))
            mTwoPane = bundle.getBoolean("isTwoPane");


            mySteps = new String[recipeData.getSteps().size()];

            int k = 0;

            //getting primary colors for stepper
            int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
            int colorPrimaryDark = ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimaryDark);

            for (int i = mySteps.length -1; i <= 0; i--) {

                mySteps[k] = recipeData.getSteps().get(i).getShortDescription();
                k++;

            }


            verticalStepperForm = view.findViewById(R.id.vertical_stepper_form);


            // Setting up and initializing the form
            VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, getActivity())
                    .primaryColor(colorPrimary)
                    .primaryDarkColor(colorPrimaryDark)
                    .materialDesignInDisabledSteps(true)
                    .displayBottomNavigation(false) // It is true by default, so in this case this line is not necessary
                    .init();

            //setting data to the tiles because this library has issues
            for(int i = 0; i < mySteps.length; i++) {

                verticalStepperForm.setStepTitle(i, recipeData.getSteps().get(i).getShortDescription());

            }


            setupBottomSheet(view);

            changeRecipeButton = view.findViewById(R.id.changeRecipe);

            changeRecipeButton.setOnClickListener(view1 -> {

                //mListener.onRecipeChange(recipeData.getId());

                Toast.makeText(getContext(), recipeData.getId() + " getting recipe id", Toast.LENGTH_SHORT).show();

                //Trying to save the recipe
                RecipeWidget.selectRecipe(recipeData.getId(), getContext());

            });


        }

        else {

            //Error Condition
            //noDetails.setVisibility(View.VISIBLE);
            verticalStepperForm.setVisibility(View.GONE);

        }

    }

    private void setupBottomSheet(View view) {

        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);



        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * Methods for implementing Material Stepper form
     * @param stepNumber
     * @return
     */
    @Override
    public View createStepContentView(int stepNumber) {



        //inflating view from XML file:R.layout.step_layout and setting text to the TextView in it.
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.stpper_layout, null, false);
        TextView stepShortDescription = view.findViewById(R.id.stepper_description_text_sl);
        stepShortDescription.setText(recipeData.getSteps().get(stepNumber).getShortDescription());

        if(mTwoPane) {
            //code for tablet layout
            view.setOnClickListener(view1 -> {

                mListener.onFragmentInteraction(stepNumber);
                Log.e("I am here", stepNumber + "");
            });


        } else {
            //code for phones
            view.setOnClickListener(view1 -> {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(getContext().getPackageName(), Parcels.wrap(recipeData));
                intent.putExtra("position", stepNumber);
                startActivity(intent);
            });

        }

        return view;
    }

    @Override
    public void onStepOpening(int stepNumber) {

        if(mTwoPane && stepNumber < mySteps.length)
        mListener.onFragmentInteraction(stepNumber);

        verticalStepperForm.setActiveStepAsCompleted();

    }

    @Override
    public void sendData() {

        getActivity().finish();

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int position);

        void onRecipeChange(Integer id);
    }
}
