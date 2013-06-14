package org.thobe.java.tooling;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.thobe.testing.subprocess.SubprocessTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

@RunWith(SubprocessTestRunner.class)
@SubprocessTestRunner.SubprocessConfiguration(RunWithToolingAgent.class)
public class CallFrameTest
{
    ToolingInterface tools = ToolingInterface.getToolingInterface();

    @Test
    public void shouldAccessLiveCallFrameOfCurrentMethod() throws Exception
    {
        // given
        int i = doNotInline( 5 );
        long j = doNotInline( 666l );
        byte b = doNotInline( (byte) 7 );
        short s = doNotInline( (short) 60 );
        boolean z = doNotInline( false );
        char c = doNotInline( 'x' );
        float f = doNotInline( 3.14f );
        double d = doNotInline( 10.0 );

        // when
        CallFrame frame = tools.getCallFrame( 0 );

        // then
        assertEquals( i, frame.getLocal( "i" ) );
        i = 14;
        assertEquals( i, frame.getLocal( "i" ) );
        assertEquals( j, frame.getLocal( "j" ) );
        assertEquals( b, frame.getLocal( "b" ) );
        assertEquals( s, frame.getLocal( "s" ) );
        assertEquals( z, frame.getLocal( "z" ) );
        assertEquals( c, frame.getLocal( "c" ) );
        assertEquals( f, frame.getLocal( "f" ) );
        assertEquals( d, frame.getLocal( "d" ) );
        assertSame( this, frame.getThis() );
        assertSame( frame, frame.getLocal( "frame" ) );
    }

    @Test
    public void shouldSetLocalInt() throws Exception
    {
        // given
        int local = doNotInline( 0x0099cc );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", 17 );
        // then
        assertEquals( 17, local );
    }

    @Test
    public void shouldSetLocalLong() throws Exception
    {
        // given
        long local = doNotInline( 0x0099cc );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", 17l );
        // then
        assertEquals( 17, local );
    }

    @Test
    public void shouldSetLocalByte() throws Exception
    {
        // given
        byte local = doNotInline( (byte) 122 );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", (byte) 17 );
        // then
        assertEquals( 17, local );
    }

    @Test
    public void shouldSetLocalShort() throws Exception
    {
        // given
        short local = doNotInline( (short) 1024 );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", (short) 17 );
        // then
        assertEquals( 17, local );
    }

    @Test
    public void shouldSetLocalBoolean() throws Exception
    {
        // given
        boolean local = doNotInline( true );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", false );
        // then
        assertFalse( local );
    }

    @Test
    public void shouldSetLocalChar() throws Exception
    {
        // given
        char local = doNotInline( 'x' );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", 'y' );
        // then
        assertEquals( 'y', local );
    }

    @Test
    public void shouldSetLocalFloat() throws Exception
    {
        // given
        float local = doNotInline( 11.6f );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", 3.14f );
        // then
        assertEquals( 3.14f, local, 0.0 );
    }

    @Test
    public void shouldSetLocalDouble() throws Exception
    {
        // given
        double local = doNotInline( 3.14 );
        // when
        tools.getCallFrame( 0 ).setLocal( "local", 2.666 );
        // then
        assertEquals( 2.666, local, 0.0 );
    }

    @Test
    public void shouldDetachFrameWhenExitingMethod() throws Exception
    {
        // given
        String message = "hello world";
        CallFrame frame = method( message );
        // when
        Object value = frame.getLocal( "value" );
        // then
        assertSame( message, value );
    }

    @Test
    public void shouldSetLocalVariablesOfLiveFrame() throws Exception
    {
        // given
        String local = doNotInline( "pre" );
        CallFrame frame = tools.getCallFrame( 0 );

        // when
        frame.setLocal( "local", "post" );

        // then
        assertEquals( "post", local );
    }

    @Test
    public void shouldThrowExceptionWhenAttemptingToSetLocalVariablesOnDetachedFrame() throws Exception
    {
        // given
        CallFrame frame = method( "fish" );

        // when
        try
        {
            frame.setLocal( "value", "chicken" );
            fail( "expected exception" );
        }
        // then
        catch ( IllegalStateException expected )
        {
            assertEquals( "Cannot set local variable on detached frame.", expected.getMessage() );
        }
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenAttemptingToAssignPrimitiveToNull() throws Exception
    {
        // given
        int i = doNotInline( 5 );
        long j = doNotInline( 666l );
        byte b = doNotInline( (byte) 7 );
        short s = doNotInline( (short) 60 );
        boolean z = doNotInline( false );
        char c = doNotInline( 'x' );
        float f = doNotInline( 3.14f );
        double d = doNotInline( 10.0 );

        CallFrame frame = tools.getCallFrame( 0 );

        for ( String local : new String[]{"i", "j", "b", "s", "z", "c", "f", "d"} )
        {
            // when
            try
            {
                frame.setLocal( local, null );

                fail( "expected exception for: " + local );
            }
            // then
            catch ( NullPointerException e )
            {
                assertEquals( local, e.getMessage() );
            }
        }
        assertEquals( i, frame.getLocal( "i" ) );
        assertEquals( j, frame.getLocal( "j" ) );
        assertEquals( b, frame.getLocal( "b" ) );
        assertEquals( s, frame.getLocal( "s" ) );
        assertEquals( z, frame.getLocal( "z" ) );
        assertEquals( c, frame.getLocal( "c" ) );
        assertEquals( f, frame.getLocal( "f" ) );
        assertEquals( d, frame.getLocal( "d" ) );
    }

    @Test
    public void shouldThrowClassCastExceptionWhenAttemptingToSetLocalOfWrongType() throws Exception
    {
        // given
        int i = doNotInline( 5 );
        long j = doNotInline( 666l );
        byte b = doNotInline( (byte) 7 );
        short s = doNotInline( (short) 60 );
        boolean z = doNotInline( false );
        char c = doNotInline( 'x' );
        float f = doNotInline( 3.14f );
        double d = doNotInline( 10.0 );
        String string = doNotInline( "value" );

        CallFrame frame = tools.getCallFrame( 0 );

        for ( String local : new String[]{"i", "j", "b", "s", "z", "c", "f", "d", "string"} )
        {
            // when
            try
            {
                frame.setLocal( local, new Object() );

                fail( "expected exception for: " + local );
            }
            // then
            catch ( ClassCastException e )
            {
                assertEquals( String.format( "\"%s\" may not be java.lang.Object, must be instance of %s", local,
                                             frame.getLocal( local ).getClass().getName() ), e.getMessage() );
            }
        }
        assertEquals( 5, i );
        assertEquals( 666l, j );
        assertEquals( (byte) 7, b );
        assertEquals( (short) 60, s );
        assertEquals( false, z );
        assertEquals( 'x', c );
        assertEquals( 3.14f, f, 0.0 );
        assertEquals( 10.0, d, 0.0 );
        assertEquals( "value", string );
    }

    private CallFrame method( String value )
    {
        assertNotNull( value );
        return tools.getCallFrame( 0 );
    }

    private static <T> T doNotInline( T value )
    {
        return value;
    }
}
