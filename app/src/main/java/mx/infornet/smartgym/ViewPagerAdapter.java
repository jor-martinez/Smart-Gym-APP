package mx.infornet.smartgym;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] childFragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        childFragments = new Fragment[]{
                new EditarDatosFragment(),
                new EditarPasswordFragment()
        };
    }

    @Override
    public Fragment getItem(int position) {

        return childFragments[position];
    }

    @Override
    public int getCount() {
        return childFragments.length; //three fragments
    }


    @Override
    public CharSequence getPageTitle(int position) {


        switch (position){
            case 0:
                return "Datos";
            case 1:
                return "Contraseña";
            default:
                return null;
        }
    }
}