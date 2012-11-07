#ifndef __CCNUMBER_H__
#define __CCNUMBER_H__

#include "CCObject.h"

NS_CC_BEGIN

/**
 * @addtogroup data_structures
 * @{
 */

class CC_DLL CCNumber : public CCObject
{
public:
    CCNumber(float v)
        : m_nValue(v) {}
    float getValue() const {return m_nValue;}

    // @deprecated: This interface will be deprecated sooner or later.
    CC_DEPRECATED_ATTRIBUTE static CCNumber* integerWithInt(float v)
    {
        return CCNumber::create(v);
    }

    static CCNumber* create(float v)
    {
        CCNumber* pRet = new CCNumber(v);
        pRet->autorelease();
        return pRet;
    }
private:
    float m_nValue;
};

// end of data_structure group
/// @}

NS_CC_END

#endif /* __CCNumber_H__ */
