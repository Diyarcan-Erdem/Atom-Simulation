package  Diyarcan.Atom.Simulation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AtomController {

    private List<AtomModel> atoms = new ArrayList<>();
    private final double EPSILON = 10.0;
    private final double SIGMA = 40.0;
    private final double DT = 0.05;

    public AtomController() {
        atoms.add(new AtomModel(300, 300, 0.5, -0.2));
        atoms.add(new AtomModel(380, 320, -0.4, 0.3));
        atoms.add(new AtomModel(340, 250, 0.2, 0.5));
    }

    @GetMapping("/api/atoms")
    public List<AtomModel> getAndUpdateAtoms() {
        int n = atoms.size();
        
        for (AtomModel a : atoms) {
            a.fx = 0; a.fy = 0;
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                AtomModel a1 = atoms.get(i);
                AtomModel a2 = atoms.get(j);

                double dx = a2.x - a1.x;
                double dy = a2.y - a1.y;
                double r2 = dx * dx + dy * dy;

                if (r2 < 1.0) r2 = 1.0;

                double r = Math.sqrt(r2);
                double sr = SIGMA / r;
                double sr6 = Math.pow(sr, 6);
                double sr12 = sr6 * sr6;

                double f_over_r = 48.0 * EPSILON * (sr12 - 0.5 * sr6) / r2;

                a1.fx -= f_over_r * dx;
                a1.fy -= f_over_r * dy;
                a2.fx += f_over_r * dx;
                a2.fy += f_over_r * dy;
            }
        }

        for (AtomModel a : atoms) {
            a.vx += a.fx * DT;
            a.vy += a.fy * DT;
            a.x += a.vx * DT;
            a.y += a.vy * DT;

            if (a.x < 20 || a.x > 780) { a.vx *= -1; a.x = Math.max(20, Math.min(780, a.x)); }
            if (a.y < 20 || a.y > 580) { a.vy *= -1; a.y = Math.max(20, Math.min(580, a.y)); }
        }

        return atoms;
    }
}