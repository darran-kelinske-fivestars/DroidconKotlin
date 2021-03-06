package co.touchlab.sessionize.sponsors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.sessionize.R
import co.touchlab.sessionize.SponsorSessionInfo
import co.touchlab.sessionize.SponsorSessionModel
import com.squareup.picasso.Picasso

class SponsorSessionFragment : Fragment() {

    val sponsorId: String by lazy {
        arguments?.let {
            val scheduleArgs = SponsorSessionFragmentArgs.fromBundle(it)
            scheduleArgs.sponsorsessionid
        } ?: ""
    }
    val groupName: String by lazy {
        arguments?.let {
            val scheduleArgs = SponsorSessionFragmentArgs.fromBundle(it)
            scheduleArgs.sponsorgroupname
        } ?: ""
    }

    lateinit var sponsorSessionViewModel: SponsorSessionViewModel
    lateinit var recycler: RecyclerView
    lateinit var sponsorSessionTitle: TextView
    lateinit var sponsorGroupName: TextView
    lateinit var sponsorImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sponsorSessionViewModel = ViewModelProviders.of(
                this,
                SponsorSessionViewModelFactory()
        )[SponsorSessionViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(
                R.layout.fragment_sponsor_session,
                container,
                false
        )

        sponsorSessionTitle= view.findViewById(R.id.sponsorSessionTitle)
        sponsorGroupName = view.findViewById(R.id.sponsorGroupName)
        recycler = view.findViewById(R.id.recycler)
        sponsorImage = view.findViewById(R.id.sponsorImage)

        val adapter = SponsorSessionAdapter(activity!!)
        recycler.adapter = adapter

        sponsorSessionViewModel.sponsorSessionModel.loadSponsorDetail({
            dataRefresh(it)
        }){
            Log.e("SponsorSessionFragment", it.message, it)
            activity?.onBackPressed()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(getActivity())
    }

    private fun dataRefresh(sponsorInfo: SponsorSessionInfo) {
        updateContent(sponsorInfo)
    }

    private fun updateContent(sponsorInfo: SponsorSessionInfo) {
        val adapter = SponsorSessionAdapter(activity!!)
        val sponsor = sponsorInfo.sponsor
        val sessionDetail = sponsorInfo.sessionDetail

        sponsorSessionTitle.text = sponsor.name
        sponsorGroupName.text = sponsor.groupName
        adapter.addHeader(sponsor.name)

        sponsor.icon.let {
            Picasso.get().load(it).into(sponsorImage)
        }

        adapter.addBody(sessionDetail)

        for (item in sponsorInfo.speakers) {
            adapter.addSpeaker(item)
        }

        recycler.adapter = adapter
    }
}
