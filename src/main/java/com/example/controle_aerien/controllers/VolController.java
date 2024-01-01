package com.example.controle_aerien.controllers;

import com.example.controle_aerien.DTO.VolDTO;
import com.example.controle_aerien.entities.Aeroport;
import com.example.controle_aerien.entities.Avion;
import com.example.controle_aerien.entities.Vol;
import com.example.controle_aerien.services.AeroportService;
import com.example.controle_aerien.services.AvionService;
import com.example.controle_aerien.services.VolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class VolController {

    @Autowired
    private VolService volService;
    @Autowired
    private AeroportService aeroportService;
    @Autowired
    private AvionService avionService;



    @GetMapping("/vols")
    public List<Vol> getAllVoll() {
        return volService.getAllVol();
    }
    @GetMapping("/volsEscale/{id}")
    public List<Vol> getAllVollEscale(@PathVariable Long id)
    {
        List<Vol> vols = volService.getAllVol();
        List <Vol> volsEscale = new ArrayList<>();
        for (Vol vol : vols) {
            if (vol.getParentVol() != null){
            if(vol.getParentVol().getId()==id)
            {
                volsEscale.add(vol);
            }

            }
        }
        return volsEscale;
    }

//    @GetMapping("/startVolGlobal/{id}")
//    public ResponseEntity<VolDTO> startVolGlobal(@PathVariable Long id)
//    {   Vol vol = volService.getVolById(id);
//        if(vol!=null) {
//            volService.StartVolGlobal(vol);
//            VolDTO volDTO = vol.toDTO();
//            return ResponseEntity.ok(volDTO);//200 OK
//        }
//        else
//            return ResponseEntity.notFound().build();//404 NOT FOUND
//    }
    @GetMapping("/startSimulation")
    public List<Vol> startSimulation()
    {
        List<Vol> vols = volService.getAllVol();
        for(Vol vol : vols)
        {
            volService.StartVolGlobal(vol);
        }
        return vols;
    }
//    @GetMapping("/startVolGlobalGloabal")
//    public void startVolGlobalGlobal()
//    {   Vol vol = volService.getVolById();
//        volService.StartVolGlobal(vol);
//    }

    @GetMapping("/vols/{id}")
    public ResponseEntity<VolDTO> getVolById(@PathVariable Long id) {
        Vol vol = volService.getVolById(id);
        if (vol != null) {
            VolDTO volDTO = vol.toDTO();
            return ResponseEntity.ok(volDTO);//200 OK
        } else
            return ResponseEntity.notFound().build();//404 NOT FOUND
    }

    @PostMapping("/create_vol")
    public ResponseEntity<String> ajouterVol(@RequestBody Vol vol) {
        try {
            volService.saveVol(vol);

            return ResponseEntity.ok("Vol ajouté avec succès!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout du vol!!");
        }
    }
    @PostMapping("/addVol")
    public ResponseEntity<String> addVol(@RequestBody Vol vol) {
        try {
            Avion avion =new Avion();

            avionService.saveAvion(avion);
            avion.setNom("avion"+avion.getId());
            aeroportService.AddAvionToAeroport(vol.getAeroportDepart().getId(),avion.getId());
            volService.addVol(vol.getAeroportDepart().getId(), vol.getAeroportArrivee().getId(),null);

            return ResponseEntity.ok("Vol ajouté avec succès!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout du vol!!");
        }
    }

    @PutMapping("/update_vol/{id}")
    public ResponseEntity<Vol> updateVol(@PathVariable Long id, @RequestBody Vol newVol) {
        Vol oldVol = volService.getVolById(id);

        if (oldVol == null) {
            return ResponseEntity.notFound().build();
        }
        oldVol.setAvion(newVol.getAvion());
        oldVol.setHeureDepart(newVol.getHeureDepart());
        oldVol.setHeureArrivee(newVol.getHeureArrivee());
        oldVol.setAeroportDepart(newVol.getAeroportDepart());
        oldVol.setAeroportArrivee(newVol.getAeroportArrivee());
        volService.saveVol(oldVol);
        return ResponseEntity.ok(oldVol);
    }

    @DeleteMapping("/delete_vol/{id}")
    public void deleteVolById(@PathVariable Long id) {
        volService.deleteVolById(id);
    }


}