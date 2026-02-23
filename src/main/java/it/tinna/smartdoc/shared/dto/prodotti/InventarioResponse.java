package it.tinna.smartdoc.shared.dto.prodotti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@SuppressWarnings("serial")
public class InventarioResponse extends DatatablesResponseDto<InventarioMagazzinoDto>
{
    @Expose
    private double valoreInventario;

    public double getValoreInventario()
    {
        return valoreInventario;
    }

    public void setValoreInventario(double valoreInventario)
    {
        this.valoreInventario = valoreInventario;
    }

}

