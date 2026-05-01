package JPA_Project.repository;

import JPA_Project.entity.Ga;
import JPA_Project.db.Tx;

import java.util.List;
import java.util.Optional;

public class GaRepository extends BaseRepository<Ga, String> {

    public Optional<Ga> findByMaGa(String maGa) {
        System.out.println("[DEBUG-GaRepo] findByMaGa called with: " + maGa);
        Ga ga = findById(maGa);
        if (ga != null) {
            System.out.println("[DEBUG-GaRepo] Found Ga - MaGa: " + ga.getMaGa() 
                + ", TenGa: " + ga.getTenGa() 
                + ", DiaChi: " + ga.getDiaChi());
        } else {
            System.out.println("[DEBUG-GaRepo] Ga not found for MaGa: " + maGa);
        }
        return Optional.ofNullable(ga);
    }

    public List<Ga> findByTenGa(String tenGa) {
        return Tx.noTx(em -> em.createQuery(
                        "select g from Ga g where g.tenGa like :tenGa",
                        Ga.class)
                .setParameter("tenGa", "%" + tenGa + "%")
                .getResultList());
    }
}
