package com.ifsp.microredesocial.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.databinding.ActivityLocationBinding
import com.ifsp.microredesocial.util.LocalizacaoHelper

class LocationActivity : AppCompatActivity(), LocalizacaoHelper.Callback{

    private lateinit var binding : ActivityLocationBinding
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonUpdateLocation.setOnClickListener {
            solicitarLocalizacao()
        }
    }


    private fun solicitarLocalizacao() {
        // pegando permissao em tempo de execucao no app

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val localizacaoHelper = LocalizacaoHelper(applicationContext)
            localizacaoHelper.obterLocalizacaoAtual(this)
        }
    }

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double)
    {
        //aguardar o tempo para localizaçao ser recebida, para so depois alterar o valor do textView
        // quando for seguro alterar o binding
        // (como é tarefa assincrona é necessario informar para alterar na Main Thread
        runOnUiThread {
            var infos = endereco.locality
            infos += "\n" + endereco.subLocality
            infos += "\n" + endereco.adminArea
            infos += "\n" + endereco.subAdminArea
            infos += "\n" + endereco.postalCode
            infos += "\n" + endereco.countryName + ", " + endereco.countryCode
            infos += "\n" + endereco.getAddressLine(0)
            binding.txtCidade.text = infos
            binding.txtLongLat.text = "Latitude: $latitude\nLongitude:$longitude"

        }
    }
    override fun onErro(mensagem: String) {
        System.out.println(mensagem)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            solicitarLocalizacao()
        } else {
            Toast.makeText(this, "Permissão de localização negada",
                Toast.LENGTH_SHORT).show()
        }
    }

}