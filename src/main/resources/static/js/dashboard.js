/**
 * 대시보드 페이지 자바스크립트
 */
document.addEventListener('DOMContentLoaded', function() {
    // 필터 버튼 이벤트 처리
    const filterButtons = document.querySelectorAll('.filter-button');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 모든 버튼에서 active 클래스 제거
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // 클릭한 버튼에 active 클래스 추가
            this.classList.add('active');
            
            // 필터링 로직 구현 (예시)
            const filterValue = this.textContent.trim();
            filterDocuments(filterValue);
        });
    });
    
    // 문서 필터링 함수 (예시)
    function filterDocuments(filter) {
        const rows = document.querySelectorAll('tbody tr');
        
        if (filter === '전체') {
            rows.forEach(row => row.style.display = '');
            return;
        }
        
        rows.forEach(row => {
            const securityTag = row.querySelector('.security-tag');
            if (!securityTag) return;
            
            const securityLevel = securityTag.textContent;
            if (filter.includes(securityLevel)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }
});
