package com.kiaev.dealer.sales;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.kiaev.dealer.dealerconsult.DealerConsultRepository;
import com.kiaev.dealer.dealerconsult.DealerConsultService;

class SalesServiceTest {

    private final SalesRepository salesRepository = Mockito.mock(SalesRepository.class);
    private final DealerConsultRepository consultRepository = Mockito.mock(DealerConsultRepository.class);
    private final DealerConsultService dealerConsultService = Mockito.mock(DealerConsultService.class);
    private final SalesService salesService = new SalesService(salesRepository, consultRepository, dealerConsultService);

    @Test
    void updateSalesAmountUpdatesOwnedSale() {
        Sales existing = new Sales();
        existing.setSalesNo(11);
        existing.setDealerNo(3);
        existing.setSalesAmount(5000000);

        when(salesRepository.findBySalesNoAndDealerNo(11, 3)).thenReturn(Optional.of(existing));
        when(salesRepository.save(any(Sales.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = salesService.updateSalesAmount(11, 3, 47000000);

        ArgumentCaptor<Sales> captor = ArgumentCaptor.forClass(Sales.class);
        verify(salesRepository).save(captor.capture());

        assertEquals(SalesService.UPDATE_SUCCESS_MESSAGE, result);
        assertEquals(47000000, captor.getValue().getSalesAmount());
    }

    @Test
    void updateSalesAmountRejectsInvalidAmountBeforeRepositoryCall() {
        String result = salesService.updateSalesAmount(11, 3, 0);

        assertEquals("판매금액을 올바르게 입력해주세요.", result);
        verifyNoInteractions(salesRepository);
    }
}
